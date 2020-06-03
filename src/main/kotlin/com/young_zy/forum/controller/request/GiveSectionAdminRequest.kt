package com.young_zy.forum.controller.request

data class GiveSectionAdminRequest(
        val sectionIds: List<Int>,
        val userIds: List<Long>
)