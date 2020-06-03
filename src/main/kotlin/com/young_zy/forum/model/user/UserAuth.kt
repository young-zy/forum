package com.young_zy.forum.model.user

import com.google.gson.Gson

data class UserAuth(
        var systemAdmin: Boolean = false,
        var sectionAdmin: Boolean = false,
        var sections: MutableList<Int> = mutableListOf(),
        var user: Boolean = false,
        var blocked: Boolean = false
) {
    constructor(userAuth: String) : this() {
        val gson = Gson()
        val userAuthTemp = gson.fromJson(userAuth, UserAuth::class.java)
        UserAuth(
                userAuthTemp.systemAdmin,
                userAuthTemp.sectionAdmin,
                userAuthTemp.sections,
                userAuthTemp.user,
                userAuthTemp.blocked
        )
    }
}