package com.young_zy.forum.model.thread

import com.young_zy.forum.model.user.SimpleUserObject
import java.time.LocalDateTime

class ThreadProjection(
        var tid: Int,
        var title: String,
        var lastReplyTime: LocalDateTime,
        var postTime: LocalDateTime,
        var question: Boolean,
        var hasBestAnswer: Boolean,
        uid: Long,
        username: String
) {
    var author = SimpleUserObject(uid, username)
}