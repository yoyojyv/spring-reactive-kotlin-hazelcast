package me.jerry.study.hazelcast

import com.hazelcast.core.HazelcastInstance
import kotlinx.coroutines.future.await
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Sort
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.util.Loggers
import java.io.Serializable
import java.time.LocalDate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@RestController
class PersonController(private val service: CachingService) {

    @GetMapping("/person")
    suspend fun getAll() = service.findAll(Sort.by("lastName", "firstName"))

    @GetMapping("/person/{id}")
    suspend fun getOne(@PathVariable id: Long): ResponseEntity<Any> {
        val person = service.findById(id)
        return if (person != null) {
            ResponseEntity(person, HttpStatus.OK)
        } else {
            ResponseEntity("", HttpStatus.NOT_FOUND)
        }
    }
}

interface PersonRepository : CoroutineCrudRepository<Person, Long>, CoroutineSortingRepository<Person, Long>

@Service
class CachingService(private val hazelcastInstance: HazelcastInstance, private val repository: PersonRepository) {

    private val cache = hazelcastInstance.getMap<Long, Person>("persons")

    private val logger = Loggers.getLogger(CachingService::class.java)

    suspend fun findById(id: Long) = cache.getAsync(id).await()
        .also {
            if (it == null) {
                logger.info("Person with id $id not found in cache")
            } else {
                logger.info("Person with id $id found in cache")
            }
        } ?: repository.findById(id)
        ?.also {
            cache.putAsync(it.id, it)
            logger.info("Person with id $id put in cache")
        }

    suspend fun findAll(sort: Sort) = repository
        .findAll(sort)
        .also { flow ->
            flow.collect {
                cache.putAsync(it.id, it)
            }
        }
}

data class Person(
    @Id
    val id: Long,
    var firstName: String,
    var lastName: String,
    var birthdate: LocalDate? = null
) : Serializable
