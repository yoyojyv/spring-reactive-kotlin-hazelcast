package me.jerry.study.hazelcast

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import reactor.blockhound.BlockHound

@SpringBootApplication
@EnableR2dbcRepositories
@EnableScheduling
class KotlinCoroutineHazelcastApplication

fun main(args: Array<String>) {
    BlockHound.builder()
//        .with {
//            it.allowBlockingCallsInside("RandomAccessFile", "readBytes")
//        }
        .install()
    @Suppress("SpreadOperator")
    runApplication<KotlinCoroutineHazelcastApplication>(*args)
}
