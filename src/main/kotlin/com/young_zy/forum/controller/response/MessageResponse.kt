package com.young_zy.forum.controller.response

import com.young_zy.forum.model.message.DetailedMessage

data class MessageResponse (
        val messages: List<DetailedMessage>
)