package com.young_zy.forum.model.thread

import com.young_zy.forum.model.user.SimpleUserObject
import java.time.LocalDateTime

class ThreadInListProjection(
        var tid: Int,
        var title: String,
        var lastReplyTime: LocalDateTime,
        var postTime: LocalDateTime,
        uid: Long,
        username: String,
        var question: Boolean,
        var hasBestAnswer: Boolean
) {
    var author = SimpleUserObject(uid, username)
}