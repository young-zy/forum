package cf.youngauthentic.forum.model.thread

import cf.youngauthentic.forum.model.user.SimpleUser
import org.springframework.data.rest.core.config.Projection
import java.sql.Timestamp

@Projection(types = [ThreadEntity::class])
interface ThreadInListProjection {
    val tid: Int
    val title: String
    val lastReplyTime: Timestamp
    val postTime: Timestamp
    val userByUid: SimpleUser
}