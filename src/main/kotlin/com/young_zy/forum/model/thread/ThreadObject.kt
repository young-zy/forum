package com.young_zy.forum.model.thread

import com.young_zy.forum.model.reply.ReplyObject
import java.sql.Timestamp

class ThreadObject(thread: ThreadProjection,
                   var replies: List<ReplyObject>,
                   var currentPage: Int,
                   var totalPage: Int) {
    val threadId: Int = thread.tid
    val title: String = thread.title
    val lastReplyTime: Timestamp = thread.lastReplyTime
    val postTime: Timestamp = thread.postTime
    val isQuestion: Boolean = thread.question
    val hasBestAnswer: Boolean = thread.hasBestAnswer
}