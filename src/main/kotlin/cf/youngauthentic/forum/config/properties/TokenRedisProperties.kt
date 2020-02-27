package cf.youngauthentic.forum.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "token.redis")
data class TokenRedisProperties(
        var host: String = "localhost",
        var port: Int = 6379,
        var dbIndex: Int = 0,
        var password: String = ""
)