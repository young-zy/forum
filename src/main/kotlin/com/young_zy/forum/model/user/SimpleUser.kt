package com.young_zy.forum.model.user

import org.springframework.data.rest.core.config.Projection

@Projection(types = [UserEntity::class])
interface SimpleUser {
    val uid: Int
    val username: String
}