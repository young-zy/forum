package cf.youngauthentic.forum.model.thread

import cf.youngauthentic.forum.model.reply.ReplyObject
import java.sql.Timestamp

class ThreadObject(thread: ThreadProjection,
                   var replies: List<ReplyObject>,
                   var currentPage: Int,
                   var totalPage: Int) {
    init {
        val threadId: Int = thread.tid
        val title: String = thread.title
        val lastReplyTime: Timestamp = thread.lastReplyTime
        val postTime: Timestamp = thread.postTime
        val isQuestion: Boolean = thread.question
        val hasBestAnswer: Boolean = thread.hasBestAnswer
    }
}