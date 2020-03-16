package cf.youngauthentic.forum.model.user

data class UserAuth(
        var isSystemAdmin: Boolean = false,
        var isSectionAdmin: Boolean = false,
        var sections: MutableList<Int> = mutableListOf(),
        var isUser: Boolean = false,
        var isBlocked: Boolean = false
)