package cf.youngauthentic.forum.service

import cf.youngauthentic.forum.model.Token
import cf.youngauthentic.forum.service.exception.AuthException
import org.springframework.stereotype.Service

@Service
class AuthService {

    /**
     * check whether user has permission to the correspond operation
     * @author young-zy
     * @param tokenObj token object of user
     * @param config AuthConfig of the operation
     * @throws AuthException when auth level is not enough
     */
    @Throws(AuthException::class)
    fun hasAuth(tokenObj: Token?, config: AuthConfig) {
        if (!config.allowAuthor && !config.allowOnlyAuthor && config.authLevel == AuthLevel.UN_LOGGED_IN) {
            return      // when un logged in user(without token) is allowed to do the operation
        }
        if (tokenObj === null) {
            throw AuthException("Not logged in")    //when operation needs log in
        }
        if (config.allowAuthor) {
            if (config.authorUid != tokenObj.uid) {       //when user is not author
                if (config.allowOnlyAuthor) {
                    throw AuthException("user ${tokenObj.uid} is not allowed to do the operation")
                }
            } else {                                      //when user is author
                return
            }
        }
        when (config.authLevel) {
            AuthLevel.UN_LOGGED_IN -> {
                return      // blocked user can also operate un logged in operations
            }
            AuthLevel.USER -> {
                if (tokenObj.auth.isBlocked) {
                    throw AuthException("user ${tokenObj.uid} is blocked, please contact admin for more info")
                }
            }
            AuthLevel.SECTION_ADMIN -> {
                if (tokenObj.auth.isBlocked) {
                    throw AuthException("user ${tokenObj.uid} is blocked, please contact admin for more info")
                }
                if (tokenObj.auth.isSystemAdmin) {
                    return
                } else if (tokenObj.auth.isSectionAdmin && (config.sectionId in tokenObj.auth.sections)) {
                    return
                } else {
                    throw AuthException("user ${tokenObj.uid} is not allowed to do the operation, minimum auth is section admin of ${config.sectionId}")
                }
            }
            AuthLevel.SYSTEM_ADMIN -> {
                if (tokenObj.auth.isBlocked) {
                    throw AuthException("user ${tokenObj.uid} is blocked, please contact admin for more info")
                }
                if (!tokenObj.auth.isSystemAdmin) {
                    throw AuthException("user ${tokenObj.uid} is not allowed to do the operation, minimum auth is system admin")
                }
            }
        }
    }
}