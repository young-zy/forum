package com.young_zy.forum.model.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("user")
data class UserEntity(
        @Column("uid")
        @Id
        var uid: Long? = null,
        @Column("username")
        var username: String = "",
        @Column("email")
        var email: String = "",
        @Column("hashedPassword")
        var hashedPassword: String = "",
        @Column("regdate")
        var regDate: LocalDate = LocalDate.MIN,
        @Column("auth")
        var auth: UserAuth = UserAuth(),
        @Column("tag_priority")
        var tagPriority: String = "0"
)