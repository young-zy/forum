package cf.youngauthentic.forum.model.thread

import cf.youngauthentic.forum.model.reply.ReplyObject
import java.sql.Timestamp

class ThreadObject(thread: ThreadProjection,
                   var replies: List<ReplyObject>,
                   var currentPage: Int,
                   var totalPage: Int) {
    var tid: Int = thread.tid
    var title: String = thread.title
    var lastReplyTime: Timestamp = thread.lastReplyTime
    var postTime: Timestamp = thread.postTime
    val isQuestion: Boolean = thread.question
    val hasBestAnswer: Boolean = thread.hasBestAnswer
}