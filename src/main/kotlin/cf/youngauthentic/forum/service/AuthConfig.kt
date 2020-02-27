package cf.youngauthentic.forum.service

data class AuthConfig(
        var authLevel: Enum<AuthLevel>,
        var allowSelf: Boolean,
        var authType: AuthType,
        var targetUid: Int = 0
)


