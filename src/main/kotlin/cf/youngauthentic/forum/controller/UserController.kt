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
            val rateLimit = rateLimitService.hasReserve(headers["X-Real-IP"]
                    ?: throw Exception("X-Real-IP in header not found, please check balance loader settings"))
            responseHeaders.add("X-RateLimit-Limit", "500")
            responseHeaders.add("X-RateLimit-Remaining", rateLimit.timesRemain.toString())
            responseHeaders.add("X-RateLimit-Reset", rateLimit.resetTimestamp.toString())
            if (rateLimit.timesRemain == -1) {
                throw RateLimitExceededException()
            }
            responseBody = UserResponse(userService.getDetailedUser(userId.toInt()))
        } catch (e: AuthException) {
            responseStatus = HttpStatus.UNAUTHORIZED
        } catch (e: RateLimitExceededException) {
            responseStatus = HttpStatus.TOO_MANY_REQUESTS
        } catch (e: Exception) {
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR
        } finally {
            logger.debug("test")
            return ResponseEntity
                    .status(responseStatus)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PostMapping("/user/login")
    fun login(): ResponseEntity<*> {
        val headers = HttpHeaders()
        return try {


            ResponseEntity.status(HttpStatus.OK).headers(headers).body(Response())
        } catch (e: PasswordIncorrectException) {
            ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .headers(headers)
                    .body(Response())
        } catch (e: UsernameIncorrectException) {
            ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .headers(headers)
                    .body(Response())
        }
    }

    @PostMapping("/user/register")
    fun register(@PathVariable userId: String): Response {

        return Response()
    }
}