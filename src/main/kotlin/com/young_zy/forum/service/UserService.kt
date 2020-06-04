package com.young_zy.forum.service

import com.young_zy.forum.model.user.DetailedUser
import com.young_zy.forum.model.user.UserAuth
import com.young_zy.forum.model.user.UserEntity
import com.young_zy.forum.repo.UserNativeRepository
import com.young_zy.forum.service.exception.AuthException
import com.young_zy.forum.service.exception.NotAcceptableException
import com.young_zy.forum.service.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.time.LocalDate

@Service
class UserService {

    @Autowired
    private lateinit var userNativeRepository: UserNativeRepository

    @Autowired
    private lateinit var loginService: LoginService

    @Autowired
    private lateinit var regexService: RegexService

    @Autowired
    private lateinit var sectionService: SectionService

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var transactionalOperator: TransactionalOperator

    /**
     * get UserEntity through provided username
     * @param username username of the user
     * @return UserEntity
     */
    suspend fun getUser(username: String): UserEntity? {
        return userNativeRepository.findByUsername(username)
    }

    /**
     * get UserEntity through provided uid
     * @param uid uid of the user
     * @return UserEntity
     */
    suspend fun getUser(uid: Long): UserEntity {
        return userNativeRepository.findByUid(uid) ?: throw NotFoundException("uid $uid not found")
    }

    /**
     * check if username already exists
     * @param username designated username tobe queried
     * @return true if username already exist
     */
    suspend fun existsUsername(username: String): Boolean {
        return userNativeRepository.existsByUsername(username)
    }

    suspend fun getDetailedUser(token: String, uid: Long?): DetailedUser {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER, allowAuthor = true))
        return userNativeRepository.findDetailedUserEntityByUid(uid ?: tokenObj!!.uid)
                ?: throw NotFoundException("uid $uid not found")
    }

    /**
     * @author young-zy
     * @param username username of user
     * @param password password of user
     * @param email email of user
     * @throws IllegalArgumentException when any of the parameter doesn't match regex requirement
     * @throws NotAcceptableException when the username already exists
     */
    @Throws(IllegalArgumentException::class, NotAcceptableException::class)
    suspend fun register(username: String, password: String, email: String) {
        transactionalOperator.executeAndAwait {
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
                user.regDate = LocalDate.now()
                user.email = email
                userNativeRepository.insert(user)
            }
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
    @Throws(NotAcceptableException::class, NotAcceptableException::class, IllegalArgumentException::class, AuthException::class)
    suspend fun userInfoUpdate(token: String, originalPassword: String, newPassword: String?, newUsername: String?, newEmail: String?) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER))
        transactionalOperator.executeAndAwait {
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
            if (newUsername != userEntity.username && existsUsername(newUsername ?: "")) {
                throw NotAcceptableException("Username $newUsername already exists")
            }
            userEntity.username = newUsername ?: userEntity.username
            if (newPassword != null) {
                userEntity.hashedPassword = PasswordHash.createHash(newPassword)
            }
            userNativeRepository.update(userEntity)
        }
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
    @Throws(NotFoundException::class, AuthException::class)
    suspend fun giveSystemAdmin(token: String, userIds: List<Long>) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        transactionalOperator.executeAndAwait {
            userIds.forEach {
                val user = userNativeRepository.findByUid(it) ?: throw NotFoundException("user with $it not found")
                user.auth.systemAdmin = false
                userNativeRepository.update(user)
            }
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
    @Throws(NotFoundException::class, AuthException::class)
    suspend fun revokeSystemAdmin(token: String, userIds: List<Long>) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        transactionalOperator.executeAndAwait {
            userIds.forEach {
                val user = userNativeRepository.findByUid(it) ?: throw NotFoundException("user with $it not found")
                user.auth.systemAdmin = false
                userNativeRepository.update(user)
            }
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
    @Throws(NotFoundException::class, AuthException::class)
    suspend fun giveSectionAdmin(token: String, userIds: List<Long>, sectionIds: List<Long>) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        transactionalOperator.executeAndAwait {
            userIds.forEach {
                val user = userNativeRepository.findByUid(it) ?: throw NotFoundException("user with $it not found")
                user.auth.sectionAdmin = true
                sectionIds.forEach { sid ->
                    if (!sectionService.hasSection(sid)) {
                        throw NotFoundException("section $sid not found")
                    }
                    user.auth.sections.add(sid)
                }
                userNativeRepository.update(user)
            }
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
    @Throws(NotFoundException::class, AuthException::class)
    suspend fun revokeSectionAdmin(token: String, userIds: List<Long>, sectionIds: List<Long>) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        transactionalOperator.executeAndAwait {
            userIds.forEach {
                val user = userNativeRepository.findByUid(it) ?: throw NotFoundException("user with $it not found")
                sectionIds.forEach { sid ->
                    if (!sectionService.hasSection(sid)) {
                        throw NotFoundException("section $sid not found")
                    }
                    user.auth.sections.remove(sid)
                }
                if (user.auth.sections.isEmpty()) {
                    user.auth.sectionAdmin = false
                }
                userNativeRepository.update(user)
                //TODO revoke user token
//            val userToken = loginService.getToken()
            }
        }
    }
}