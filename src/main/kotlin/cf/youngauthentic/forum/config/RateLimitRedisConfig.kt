package cf.youngauthentic.forum.config

import cf.youngauthentic.forum.config.properties.RateLimitRedisProperties
import cf.youngauthentic.forum.model.RateLimit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class RateLimitRedisConfig(
        @Autowired val rateLimitRedisProperties: RateLimitRedisProperties
) {
    @Bean
    fun rateLimitRedisConnectionFactory(): RedisConnectionFactory {
        val conf = RedisStandaloneConfiguration(rateLimitRedisProperties.host, rateLimitRedisProperties.port)
        conf.setPassword(rateLimitRedisProperties.password)
        conf.database = rateLimitRedisProperties.dbIndex
        return LettuceConnectionFactory(conf)
    }

    @Bean
    fun rateLimitRedisTemplate(): RedisTemplate<String, RateLimit>? {
        val template = RedisTemplate<String, RateLimit>()
        template.setConnectionFactory(rateLimitRedisConnectionFactory())
        template.setEnableTransactionSupport(true)
        return template
    }
}