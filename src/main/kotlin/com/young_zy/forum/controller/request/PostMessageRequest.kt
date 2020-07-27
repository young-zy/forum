package com.young_zy.forum.controller.request

data class PostMessageRequest(
        val to: Long,
        val messageText: String
)