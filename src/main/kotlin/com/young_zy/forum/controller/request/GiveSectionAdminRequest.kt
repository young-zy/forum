package com.young_zy.forum.controller.request

data class GiveSectionAdminRequest(
        val sectionIds: List<Long>,
        val userIds: List<Long>
)