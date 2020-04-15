package com.young_zy.forum.controller.request

data class PostThreadRequest(
        val sectionId: Int,
        val title: String,
        val content: String,
        val isQuestion: Boolean = false
)