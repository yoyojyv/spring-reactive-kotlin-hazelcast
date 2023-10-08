package me.jerry.study.hazelcast

import com.hazelcast.core.HazelcastInstance
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Sort
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.util.Loggers
import java.io.Serializable
import java.time.LocalDate

@RestController
class PersonController(private val service: CachingService) {

    @GetMapping("/person")
    fun getAll() = service.findAll(Sort.by("lastName", "firstName"))

    @GetMapping("/person/{id}")
    fun getOne(@PathVariable id: Long): Mono<Person> {
        val mono = service.findById(id)
            .switchIfEmpty(Mono.error { ResponseStatusException(HttpStatus.NOT_FOUND) })
        return mono
    }
}

interface PersonRepository : ReactiveCrudRepository<Person, Long>, ReactiveSortingRepository<Person, Long>

@Service
class CachingService(private val hazelcastInstance: HazelcastInstance, private val repository: PersonRepository) {

    private val cache = hazelcastInstance.getMap<Long, Person>("persons")

    private val logger = Loggers.getLogger(CachingService::class.java)

    fun findById(id: Long) = Mono.fromCompletionStage { cache.getAsync(id) }
        .doOnNext { logger.info("Person with id $id found in cache") }
        .switchIfEmpty(
            repository.findById(id)
                .doOnNext {
                    cache.putAsync(it.id, it)
                    logger.info("Person with id $id put in cache")
                }

        )

    fun findAll(sort: Sort) = repository
        .findAll(sort)
        .doOnNext { cache.putAsync(it.id, it) }
}

data class Person(
    @Id
    val id: Long,
    var firstName: String,
    var lastName: String,
    var birthdate: LocalDate? = null
) : Serializable
