package com.young_zy.forum.model.user

import org.springframework.data.rest.core.config.Projection
import java.util.*

@Projection(types = [UserEntity::class], name = "DetailedUser")
interface DetailedUser {
    val uid: Int
    val username: String
    val email: String
    val auth: UserAuth
    val regDate: Date
}