package com.young_zy.forum.model.thread


import com.young_zy.forum.model.user.SimpleUser
import org.springframework.data.rest.core.config.Projection
import java.sql.Timestamp

@Projection(types = [ThreadEntity::class])
interface ThreadProjection {
    var tid: Int
    var title: String
    var lastReplyTime: Timestamp
    var postTime: Timestamp
    var question: Boolean
    var hasBestAnswer: Boolean
    var author: SimpleUser
}