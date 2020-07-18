package com.young_zy.forum.model.message

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDateTime

data class MessageEntity(
        @Id
        @Column("messageId")
        val messageId: Long? = -1,
        @Column("from")
        var from: Long,
        @Column("to")
        var to: Long,
        @Column("messageText")
        var messageText: String,
        @Column("unread")
        var unread: Boolean,
        @Column("sendTime")
        var sendTime: LocalDateTime = LocalDateTime.now()
)