package com.young_zy.forum.controller.request

data class UserUpdateRequest(
        val username: String?,
        val password: String,
        val email: String?,
        val newPassword: String?
)