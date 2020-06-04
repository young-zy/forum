package com.young_zy.forum.service

/**
 * configuration of auth level needed by the operation
 * @author young-zy
 * @param authLevel the minimum auth level of the operator to do the operation
 * @param allowAuthor allow the user with authorUid to operate, doesn't affect authLevel. Be aware that even blocked user won't be able to operate.
 * @param allowOnlyAuthor only allow the author to operate, ignores the authLevel(which means even system admin cannot operate). will be check only when allowAuthor is set to true.
 * @param authorUid uid of the author
 * @param sectionId sectionId of the section (use when the authLevel is equal or below )
 */
data class AuthConfig(
        var authLevel: Enum<AuthLevel> = AuthLevel.SYSTEM_ADMIN,
        var allowAuthor: Boolean = false,
        var allowOnlyAuthor: Boolean = false,
        var authorUid: Long = -1,
        var sectionId: Long = -1
)


