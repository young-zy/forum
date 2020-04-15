package com.young_zy.forum.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "rate.redis")
data class RateLimitRedisProperties(
        var host: String = "localhost",
        var port: Int = 6379,
        var dbIndex: Int = 1,
        var password: String = ""
)