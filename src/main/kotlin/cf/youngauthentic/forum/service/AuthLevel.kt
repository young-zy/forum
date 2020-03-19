package cf.youngauthentic.forum.service

enum class AuthLevel {
    SYSTEM_ADMIN,
    SECTION_ADMIN,
    USER,
    BLOCKED_USER,
    UN_LOGGED_IN
}