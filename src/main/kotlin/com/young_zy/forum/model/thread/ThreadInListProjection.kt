package com.young_zy.forum.model.thread

import com.young_zy.forum.model.user.SimpleUserObject
import java.time.LocalDateTime

class ThreadInListProjection(
        var tid: Long,
        var title: String,
        var lastReplyTime: LocalDateTime,
        var postTime: LocalDateTime,
        var threadContent: String,
        uid: Long,
        username: String,
        var question: Boolean,
        var bestAnswer: Long?
) {
    var author = SimpleUserObject(uid, username)
}