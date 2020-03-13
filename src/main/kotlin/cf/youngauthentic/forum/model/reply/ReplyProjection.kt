package cf.youngauthentic.forum.model.reply

import cf.youngauthentic.forum.model.user.SimpleUser
import org.springframework.data.rest.core.config.Projection
import java.sql.Timestamp

@Projection(types = [ReplyEntity::class])
interface ReplyProjection {
    val rid: Int
    val replyContent: String
    val replyTime: Timestamp
    val lastEditTime: Timestamp
    val priority: Double
    val isBestAnswer: Boolean
    val userByUserId: SimpleUser
}