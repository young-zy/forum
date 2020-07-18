package com.young_zy.forum.model.message

import java.time.LocalDateTime

data class DetailedMessage(
        val messageId: Long,
        val senderUid: Long,
        val username: String,
        val isRead: Boolean,
        val messageText: String,
        val sendTime: LocalDateTime
)