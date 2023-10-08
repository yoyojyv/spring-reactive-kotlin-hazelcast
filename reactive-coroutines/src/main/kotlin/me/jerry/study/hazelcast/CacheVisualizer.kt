package me.jerry.study.hazelcast

import com.hazelcast.core.HazelcastInstance
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalTime

@Component
class CacheVisualizer(private val hazelcastInstance: HazelcastInstance) {

    @Scheduled(fixedDelay = 10000)
    fun cachePeek() {
        val personCache = hazelcastInstance.getMap<Long, Person>("persons")
        println(LocalTime.now())
        println("size: " + personCache.size)
        personCache.forEach { k: Any, v: Any ->
            println("    " + k.toString() + ":" + v.toString())
        }
        println()
    }
}
