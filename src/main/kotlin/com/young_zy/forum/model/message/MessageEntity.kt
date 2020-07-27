package com.young_zy.forum.model.message

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("message")
data class MessageEntity(
        @Id
        @Column("messageId")
        val messageId: Long? = null,
        @Column("sender")
        var from: Long,
        @Column("receiver")
        var to: Long,
        @Column("messageText")
        var messageText: String,
        @Column("unread")
        var unread: Boolean = true,
        @Column("sendTime")
        var sendTime: LocalDateTime = LocalDateTime.now()
)