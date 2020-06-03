package com.young_zy.forum.model.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.sql.Date

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
        @Column("regDate")
        var regDate: Date = Date(0),
        @Column("auth")
        var auth: UserAuth = UserAuth(),
        @Column("tag_priority")
        var tagPriority: String = "0"
)