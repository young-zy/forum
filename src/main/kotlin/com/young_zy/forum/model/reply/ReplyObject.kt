package com.young_zy.forum.model.reply

import com.young_zy.forum.model.user.SimpleUserObject
import java.time.LocalDateTime

class ReplyObject(
        val replyId: Int,
        val replyContent: String,
        val replyTime: LocalDateTime,
        val lastEditTime: LocalDateTime,
        val priority: Double,
        val bestAnswer: Boolean,
        val upVote: Int,
        uid: Long,
        username: String,
        var vote: Int
) {
    val user: SimpleUserObject = SimpleUserObject(uid, username)
}