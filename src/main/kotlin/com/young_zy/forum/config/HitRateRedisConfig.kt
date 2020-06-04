package com.young_zy.forum.config

import com.young_zy.forum.config.properties.HitRateRedisProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class HitRateRedisConfig(@Autowired val hitRateRedisProperties: HitRateRedisProperties) {
    @Bean
    fun hitRateRedisConnectionFactory(): RedisConnectionFactory {
        val conf = RedisStandaloneConfiguration(hitRateRedisProperties.host, hitRateRedisProperties.port)
        conf.setPassword(hitRateRedisProperties.password)
        conf.database = hitRateRedisProperties.dbIndex
        return LettuceConnectionFactory(conf)
    }

    @Bean
    fun hitRateRedisTemplate(): RedisTemplate<String, Long>? {
        val template = RedisTemplate<String, Long>()
        template.setConnectionFactory(hitRateRedisConnectionFactory())
        template.setEnableTransactionSupport(true)
        val stringRedisSerializer = StringRedisSerializer()
        template.keySerializer = stringRedisSerializer
//        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(Int::class.java)
//        template.valueSerializer = jackson2JsonRedisSerializer
        return template
    }
}