package cf.youngauthentic.forum.service

import cf.youngauthentic.forum.model.user.DetailedUser
import cf.youngauthentic.forum.model.user.UserAuth
import cf.youngauthentic.forum.model.user.UserEntity
import cf.youngauthentic.forum.repo.UserRepository
import cf.youngauthentic.forum.service.exception.AuthException
import cf.youngauthentic.forum.service.exception.NotAcceptableException
import cf.youngauthentic.forum.service.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Date

@Service
class UserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var loginService: LoginService

    @Autowired
    private lateinit var regexService: RegexService

    @Autowired
    private lateinit var sectionService: SectionService

    @Autowired
    private lateinit var authService: AuthService

    /**
     * get UserEntity through provided username
     * @param username username of the user
     * @return UserEntity
     */
    fun getUser(username: String): UserEntity? {
        return userRepository.findByUsername(username)
    }

    /**
     * get UserEntity through provided uid
     * @param uid uid of the user
     * @return UserEntity
     */
    fun getUser(uid: Int): UserEntity {
        return userRepository.findByUid(uid) ?: throw NotFoundException("uid $uid not found")
    }

    /**
     * check if username already exists
     * @param username designated username tobe queried
     * @return true if username already exist
     */
    fun existsUsername(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    fun getDetailedUser(token: String, uid: Int): DetailedUser {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER, allowAuthor = true))
        return userRepository.findDetailedUserEntityByUid(uid) ?: throw NotFoundException("uid $uid not found")
    }

    /**
     * @author young-zy
     * @param username username of user
     * @param password password of user
     * @param email email of user
     * @throws IllegalArgumentException when any of the parameter doesn't match regex requirement
     * @throws NotAcceptableException when the username already exists
     */
    @Transactional
    @Throws(IllegalArgumentException::class, NotAcceptableException::class)
    fun register(username: String, password: String, email: String) {
        if (existsUsername(username)) {
            throw NotAcceptableException("username $username already exists")
        } else {
            regexService.validateUsername(username)
            regexService.validatePassword(password)
            regexService.validateEmail(email)
            val userAuth = UserAuth(user = true)
            val user = UserEntity()
            user.username = username
            user.hashedPassword = PasswordHash.createHash(password)
            user.auth = userAuth
            user.regDate = Date(System.currentTimeMillis())
            user.email = email
            userRepository.save(user)
        }
    }

    /**
     * @author young-zy
     * @param token token of user
     * @param originalPassword user's original password
     * @param newPassword user's new password -- can be null
     * @param newUsername user's new username -- can be null
     * @param newEmail user's new email address -- can be null
     * @throws NotAcceptableException when username already exists or password is incorrect
     * @throws IllegalArgumentException when password or email doesn't fit regex
     */
    @Transactional
    @Throws(NotAcceptableException::class, NotAcceptableException::class, IllegalArgumentException::class, AuthException::class)
    fun userInfoUpdate(token: String, originalPassword: String, newPassword: String?, newUsername: String?, newEmail: String?) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER))
        val userEntity = getUser(tokenObj!!.uid)
        if (!PasswordHash.validatePassword(originalPassword, userEntity.hashedPassword)) {
            throw NotAcceptableException("Password Incorrect")
        }
        if (newUsername != null) {
            regexService.validateUsername(newUsername)
        }
        if (newPassword != null) {
            regexService.validatePassword(newPassword)
        }
        if (newEmail != null) {
            regexService.validateEmail(newEmail)
        }
        userEntity.email = newEmail ?: userEntity.email
        if (existsUsername(newUsername ?: "")) {
            throw NotAcceptableException("Username $newUsername already exists")
        }
        userEntity.username = newUsername ?: userEntity.username
        if (newPassword != null) {
            userEntity.hashedPassword = PasswordHash.createHash(newPassword)
        }
        userRepository.save(userEntity)
    }

    /**
     * Adds the users in list's auth of System Admin
     * # NOTE: If any of the user in list does not exist, none of the changes will be commit
     * @author young-zy
     * @param token token of operating user
     * @param userIds list of userId to be given the right of system admin
     * @throws NotFoundException when user doesn't exist
     * @throws AuthException when operator's auth is not enough
     */
    @Transactional
    @Throws(NotFoundException::class, AuthException::class)
    fun giveSystemAdmin(token: String, userIds: List<Int>) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        userIds.forEach {
            val user = getUser(it)
            user.auth.systemAdmin = false
            userRepository.save(user)
        }
    }

    /**
     * Revoke the users in list's auth of System Admin
     * # NOTE: If any of the user in list does not exist, none of the changes will be commit
     * @author young-zy
     * @param token token of operating user
     * @param userIds list of userId to be given the right of system admin
     * @throws NotFoundException when user doesn't exist
     * @throws AuthException when operator's auth is not enough
     */
    @Transactional
    @Throws(NotFoundException::class, AuthException::class)
    fun revokeSystemAdmin(token: String, userIds: List<Int>) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        userIds.forEach {
            val user = getUser(it)
            user.auth.systemAdmin = false
            userRepository.save(user)
        }
    }

    /**
     * Adds the users in list's auth as section admin of the given section list
     * # NOTE: If any of the user or section in list does not exist, none of the changes will be commit
     * @author young-zy
     * @param token token of operating user
     * @param userIds list of userId to be given the right of system admin
     * @param sectionIds list of sectionId
     * @throws NotFoundException when user or section doesn't exist
     * @throws AuthException when operator's auth is not enough
     */
    @Transactional
    @Throws(NotFoundException::class, AuthException::class)
    fun giveSectionAdmin(token: String, userIds: List<Int>, sectionIds: List<Int>) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        userIds.forEach {
            val user = getUser(it)
            user.auth.sectionAdmin = true
            sectionIds.forEach { sid ->
                if (!sectionService.hasSection(sid)) {
                    throw NotFoundException("section $sid not found")
                }
                user.auth.sections.add(sid)
            }
            userRepository.save(user)
        }
    }

    /**
     * Revoke the users in list's auth as section admin of the given section list
     * # NOTE: If any of the user or section in list does not exist, none of the changes will be commit
     * ## NOTE: If any of the user is not the admin of one of the section in list, this section will be ignored for this user instead of throwing an exception
     * @author young-zy
     * @param token token of operating user
     * @param userIds list of userId to be given the right of system admin
     * @param sectionIds list of sectionId
     * @throws NotFoundException when user or section doesn't exist
     * @throws AuthException when operator's auth is not enough
     */
    @Transactional
    @Throws(NotFoundException::class, AuthException::class)
    fun revokeSectionAdmin(token: String, userIds: List<Int>, sectionIds: List<Int>) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        userIds.forEach {
            val user = getUser(it)
            sectionIds.forEach { sid ->
                if (!sectionService.hasSection(sid)) {
                    throw NotFoundException("section $sid not found")
                }
                user.auth.sections.remove(sid)
            }
            if (user.auth.sections.isEmpty()) {
                user.auth.sectionAdmin = false
            }
            userRepository.save(user)
        }
    }
}