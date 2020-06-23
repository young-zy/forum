package com.young_zy.forum.model.user

import com.google.gson.Gson

data class UserAuth(
        var systemAdmin: Boolean = false,
        var sectionAdmin: Boolean = false,
        var sections: MutableList<Long> = mutableListOf(),
        var user: Boolean = false,
        var blocked: Boolean = false
) {
    constructor(userAuth: String) : this() {
        UserAuth.build(userAuth)
    }

    companion object {
        fun build(userAuth: String): UserAuth {
            val gson = Gson()
            val userAuthTemp = gson.fromJson(userAuth, UserAuth::class.java)
            return UserAuth(
                    userAuthTemp.systemAdmin,
                    userAuthTemp.sectionAdmin,
                    userAuthTemp.sections,
                    userAuthTemp.user,
                    userAuthTemp.blocked
            )
        }
    }
}