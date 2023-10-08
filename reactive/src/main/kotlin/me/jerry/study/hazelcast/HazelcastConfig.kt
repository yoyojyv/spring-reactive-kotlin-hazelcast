package me.jerry.study.hazelcast

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HazelcastConfig {

    @Bean("hazelcastInstance")
    fun hazelcastInstance(): HazelcastInstance {
        val memberConfiguration = Config().apply {
            instanceName = "spring-hazelcast"
            clusterName = "hazelcastInstance"
            networkConfig.join.multicastConfig.isEnabled = false
            networkConfig.join.tcpIpConfig.isEnabled = true
            networkConfig.join.tcpIpConfig.members = listOf("127.0.0.1")
        }

        return Hazelcast.newHazelcastInstance(memberConfiguration)
    }
}
