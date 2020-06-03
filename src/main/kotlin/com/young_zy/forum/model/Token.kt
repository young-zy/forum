package com.young_zy.forum.model

import com.young_zy.forum.model.user.UserAuth
import org.springframework.data.annotation.Id

data class Token(
        @Id var token: String = "",
        val uid: Long = -1,
        var username: String = "",
        var auth: UserAuth = UserAuth()
)