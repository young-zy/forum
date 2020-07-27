package com.young_zy.forum.model.message

import com.young_zy.forum.model.user.DetailedUser
import com.young_zy.forum.model.user.SimpleUserObject
import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime

class DetailedMessage(
        val messageId: Long,
        sender: Long,
        username: String,
        val messageText: String,
        val unread: Boolean,
        val sendTime: LocalDateTime
){
    val sender = SimpleUserObject(sender, username)
}