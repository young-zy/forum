package cf.youngauthentic.forum.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("token")
data class Token(
        @Id var token: String,
        val uid: Int,
        var username: String,
        var auth: String
)