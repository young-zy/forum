package cf.youngauthentic.forum.service

import cf.youngauthentic.forum.model.user.DetailedUser
import cf.youngauthentic.forum.model.user.UserEntity
import cf.youngauthentic.forum.repo.UserRepository
import cf.youngauthentic.forum.service.exception.NotFoundException
import cf.youngauthentic.forum.service.exception.PasswordIncorrectException
import cf.youngauthentic.forum.service.exception.PasswordInvalidException
import cf.youngauthentic.forum.service.exception.UsernameExistsException
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
        return userRepository.findByUid(uid)
    }

    fun existsUsername(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    fun getDetailedUser(uid: Int): DetailedUser {
        return userRepository.findDetailedUserEntityByUid(uid) ?: throw NotFoundException()
    }

    @Transactional
    fun register(username: String, password: String, email: String) {
        if (existsUsername(username)) {
            throw UsernameExistsException()
        } else {
            val regex = Regex("")
            if (!regex.matches(password)) {
                throw PasswordInvalidException()
            }
            val user = UserEntity()
            user.username = username
            user.hashedPassword = PasswordHash.createHash(password)
            user.auth = "user"
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
     * @throws PasswordIncorrectException when password is incorrect
     * @throws UsernameExistsException when username already exists
     * @throws IllegalArgumentException when password or email doesn't fit regex
     */
    @Transactional
    fun userInfoUpdate(token: String, originalPassword: String, newPassword: String?, newUsername: String?, newEmail: String?) {
        val uid = loginService.getUid(token)
        val userEntity = getUser(uid)
        if (!PasswordHash.validatePassword(originalPassword, userEntity.hashedPassword)) {
            throw PasswordIncorrectException()
        }
        //TODO: implement email and password regex check
        userEntity.email = newEmail ?: userEntity.email
        if (existsUsername(newUsername ?: "")) {
            throw UsernameExistsException()
        }
        userEntity.username = newUsername ?: userEntity.username
        if (newPassword != null) {
            userEntity.hashedPassword = PasswordHash.createHash(newPassword)
        }
        userRepository.save(userEntity)
    }

}