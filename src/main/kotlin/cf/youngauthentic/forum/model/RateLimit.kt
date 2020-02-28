package cf.youngauthentic.forum.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.sql.Timestamp

@RedisHash("rateLimit")
data class RateLimit(
        @Id var userIp: String = "",
        var timesRemain: Int = -1,
        var resetTimestamp: Timestamp = Timestamp(0)
)