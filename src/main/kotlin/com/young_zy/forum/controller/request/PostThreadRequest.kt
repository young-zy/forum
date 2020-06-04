package com.young_zy.forum.controller.request

data class PostThreadRequest(
        val sectionId: Long,
        val title: String,
        val content: String,
        val isQuestion: Boolean = false
)