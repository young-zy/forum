package com.young_zy.forum.model.user

import java.time.LocalDate

data class DetailedUser(
        val uid: Long,
        val username: String,
        val email: String,
        val auth: UserAuth,
        val regDate: LocalDate
)