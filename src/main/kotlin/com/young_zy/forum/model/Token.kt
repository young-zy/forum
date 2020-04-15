package com.young_zy.forum.model

import com.young_zy.forum.model.user.UserAuth
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("token")
data class Token(
        @Id var token: String,
        val uid: Int,
        var username: String,
        var auth: UserAuth
)