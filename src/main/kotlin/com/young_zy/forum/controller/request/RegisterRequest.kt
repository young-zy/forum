package com.young_zy.forum.controller.request

data class RegisterRequest(var username: String,
                           val password: String,
                           val email: String
)