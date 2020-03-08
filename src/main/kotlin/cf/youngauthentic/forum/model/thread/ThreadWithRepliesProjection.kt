package cf.youngauthentic.forum.model.thread

import cf.youngauthentic.forum.model.reply.ReplyProjection
import cf.youngauthentic.forum.model.user.SimpleUser
import org.springframework.data.rest.core.config.Projection
import java.sql.Timestamp

@Projection(types = [Thread::class])
interface ThreadWithRepliesProjection {
    var tid: Int
    var title: String
    var lastReplyTime: Timestamp
    var postTime: Timestamp
    var user: SimpleUser
    var replies: List<ReplyProjection>
}