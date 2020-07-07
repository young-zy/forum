package com.young_zy.forum.model.user

import org.springframework.data.relational.core.mapping.Column
import java.time.LocalDate

data class DetailedUser(
        val uid: Long,
        val username: String,
        val email: String,
        val auth: UserAuth,
        @Column("regdate")
        val regDate: LocalDate
)