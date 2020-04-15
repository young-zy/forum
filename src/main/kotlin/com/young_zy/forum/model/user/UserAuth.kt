package com.young_zy.forum.model.user

data class UserAuth(
        var systemAdmin: Boolean = false,
        var sectionAdmin: Boolean = false,
        var sections: MutableList<Int> = mutableListOf(),
        var user: Boolean = false,
        var blocked: Boolean = false
)