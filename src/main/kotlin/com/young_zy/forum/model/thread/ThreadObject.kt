package com.young_zy.forum.model.thread

import com.young_zy.forum.model.TagNode
import com.young_zy.forum.model.reply.ReplyObject
import java.time.LocalDateTime

class ThreadObject(
    thread: ThreadProjection,
    var replies: List<ReplyObject>,
    var currentPage: Int,
    var totalPage: Int,
    var bestAnswer: ReplyObject?,
    var tags: List<TagNode>
) {
    val threadId: Long = thread.tid
    val sectionId: Long = thread.sid
    val title: String = thread.title
    val threadContent: String = thread.threadContent
    val lastReplyTime: LocalDateTime = thread.lastReplyTime
    val postTime: LocalDateTime = thread.postTime
    val isQuestion: Boolean = thread.question
    val bestAnswerId: Long? = thread.bestAnswer
}