package cf.youngauthentic.forum.controller

import cf.youngauthentic.forum.controller.response.Response
import cf.youngauthentic.forum.controller.response.UserResponse
import cf.youngauthentic.forum.service.LoginService
import cf.youngauthentic.forum.service.RateLimitService
import cf.youngauthentic.forum.service.UserService
import cf.youngauthentic.forum.service.exception.AuthException
import cf.youngauthentic.forum.service.exception.PasswordIncorrectException
import cf.youngauthentic.forum.service.exception.RateLimitExceededException
import cf.youngauthentic.forum.service.exception.UsernameIncorrectException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
class UserController {

    @Autowired
    lateinit var loginService: LoginService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var rateLimitService: RateLimitService

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/user/{userId}")
    fun getUser(@PathVariable userId: String,
                @RequestHeader headers: Map<String, String>): ResponseEntity<*> {
        val responseHeaders = HttpHeaders()
        var responseStatus = HttpStatus.OK
        var responseBody: Response? = null
        try {
            val rateLimit = rateLimitService.buildHeader(headers, responseHeaders)
            if (rateLimit.timesRemain <= -1) {
                throw RateLimitExceededException()
            }
            responseBody = UserResponse(userService.getDetailedUser(userId.toInt()))
        } catch (e: AuthException) {
            responseStatus = HttpStatus.UNAUTHORIZED
        } catch (e: RateLimitExceededException) {
            responseStatus = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, "API request rate exceeded the limit")
        } catch (e: Exception) {
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR
            responseBody = Response(false, e.message ?: "")
        } finally {
            logger.debug("test")
            return ResponseEntity
                    .status(responseStatus)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PostMapping("/user/login")
    fun login(
            @RequestHeader headers: Map<String, String>
    ): ResponseEntity<*> {
        val responseHeaders = HttpHeaders()
        val responseBody: Response? = null
        try {
            val rateLimit = rateLimitService.buildHeader(headers, responseHeaders)
            if (rateLimit.timesRemain <= -1) {
                throw RateLimitExceededException()
            }
        } catch (e: PasswordIncorrectException) {

        } catch (e: UsernameIncorrectException) {

        } catch (e: Exception) {

        } finally {
            return ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PostMapping("/user/register")
    fun register(@PathVariable userId: String): Response {

        return Response()
    }
}