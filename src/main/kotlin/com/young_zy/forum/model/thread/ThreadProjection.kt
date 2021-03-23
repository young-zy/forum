package com.young_zy.forum.model.thread

import com.young_zy.forum.model.user.SimpleUserObject
import java.time.LocalDateTime

class ThreadProjection(
        var tid: Long,
        var sid: Long,
        var title: String,
        var threadContent: String,
        var lastReplyTime: LocalDateTime,
        var postTime: LocalDateTime,
        var question: Boolean,
        var bestAnswer: Long?,
        uid: Long,
        username: String
) {
    var author = SimpleUserObject(uid, username)
}