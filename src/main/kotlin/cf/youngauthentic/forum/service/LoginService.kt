package cf.youngauthentic.forum.service

import cf.youngauthentic.forum.model.Token
import cf.youngauthentic.forum.model.user.UserAuth
import cf.youngauthentic.forum.model.user.UserEntity
import cf.youngauthentic.forum.service.exception.PasswordIncorrectException
import cf.youngauthentic.forum.service.exception.UsernameIncorrectException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import kotlin.math.abs

@Service
class LoginService {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var tokenRedisTemplate: RedisTemplate<String, Token>

    /**
     * @author young-zy
     * @param token token string
     * @return uid of the token if exists, else returns -1
     */
    fun getToken(token: String?): Token? {
        return (tokenRedisTemplate.opsForValue().get(token ?: ""))
    }

    /**
     * @author young-zy
     * @param token token string
     * @return uid of the token if exists, else returns -1
     */
    fun getUid(token: String): Int {
        return (tokenRedisTemplate.opsForValue().get(token) ?: return -1).uid
    }

    /**
     * @author young-zy
     * @param token token string
     * @return auth of the token if exists, else returns empty string
     */
    fun getAuth(token: String): UserAuth? {
        return (tokenRedisTemplate.opsForValue().get(token) ?: return null).auth
    }

    /**
     * check if user's token is still valid
     * # Be aware that this function refreshes token ttl if token exists
     * @author young-zy
     * @param token token string
     * @return true if token exists
     */
    fun isLoggedIn(token: String): Boolean {
        return if (tokenRedisTemplate.hasKey(token)) {
            tokenRedisTemplate.expire(token, 1, TimeUnit.DAYS)
            true
        } else {
            false
        }
    }

    /**
     * accepts username and password and returns a token if all of them are correct
     * @author young-zy
     * @param username username of user
     * @param password password of user
     * @return generated token of user
     * @throws UsernameIncorrectException when username doesn't exist
     * @throws PasswordIncorrectException when password is not correct
     */
    @Throws(PasswordIncorrectException::class, UsernameIncorrectException::class)
    fun login(username: String, password: String): String {
        val user: UserEntity = userService.getUser(username) ?: throw UsernameIncorrectException()
        return if (PasswordHash.validatePassword(password, user.hashedPassword)) {
            val random = SecureRandom()
            val bytes = ByteArray(20)
            random.nextBytes(bytes)
            val longToken = abs(random.nextLong())
            val tokenStr = longToken.toString(16)
            val token = Token(
                    "$username:$tokenStr",
                    user.uid,
                    user.username,
                    user.auth)
            tokenRedisTemplate.opsForValue().set(token.token, token, 1, TimeUnit.DAYS)
            token.token
        } else {
            throw PasswordIncorrectException()
        }
    }

    /**
     * delete token key-value from redis to logout user
     * @author young-zy
     * @param token token of user
     */
    fun logout(token: String) {
        tokenRedisTemplate.delete(token)
    }

}