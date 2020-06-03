package com.young_zy.forum.model.thread

import com.young_zy.forum.model.user.SimpleUserObject
import java.math.BigInteger
import java.time.LocalDateTime

class SearchResultDTO(
        var tid: BigInteger,
        var title: String,
        var lastReplyTime: LocalDateTime,
        var postTime: LocalDateTime,
        uid: Long,
        username: String,
        var question: Boolean,
        var hasBestAnswer: Boolean
) {
    var user = SimpleUserObject(uid, username)
}

