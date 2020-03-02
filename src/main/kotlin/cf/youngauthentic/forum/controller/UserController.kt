package cf.youngauthentic.forum.controller

import cf.youngauthentic.forum.controller.request.LoginRequest
import cf.youngauthentic.forum.controller.request.RegisterRequest
import cf.youngauthentic.forum.controller.request.UserUpdateRequest
import cf.youngauthentic.forum.controller.response.LoginResponse
import cf.youngauthentic.forum.controller.response.Response
import cf.youngauthentic.forum.controller.response.UserResponse
import cf.youngauthentic.forum.service.LoginService
import cf.youngauthentic.forum.service.RateLimitService
import cf.youngauthentic.forum.service.UserService
import cf.youngauthentic.forum.service.exception.*
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
            rateLimitService.buildHeader(headers, responseHeaders)
            responseBody = UserResponse(userService.getDetailedUser(userId.toInt()))
        } catch (e: AuthException) {
            responseStatus = HttpStatus.UNAUTHORIZED
        } catch (e: RateLimitExceededException) {
            responseStatus = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, "API request rate exceeded the limit")
        } catch (e: NotFoundException) {
            responseStatus = HttpStatus.NOT_FOUND
            responseBody = Response(false, e.message)
        } catch (e: Exception) {
            logger.warn(e.stackTrace.toString())
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(responseStatus)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PutMapping("/user")
    fun userUpdate(
            @RequestHeader headers: Map<String, String>,
            @RequestBody body: UserUpdateRequest
    ): ResponseEntity<*> {
        val responseHeaders = HttpHeaders()
        var responseStatus = HttpStatus.OK
        var responseBody: Response? = null
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            userService.userInfoUpdate(
                    headers["token"] ?: error("token not found in header"),
                    body.password,
                    body.newPassword,
                    body.username,
                    body.email)

        } catch (e: RateLimitExceededException) {
            responseStatus = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message ?: "")
        } catch (e: PasswordIncorrectException) {
            responseStatus = HttpStatus.UNAUTHORIZED
            responseBody = Response(false, e.message ?: "")
        } catch (e: IllegalArgumentException) {
            responseStatus = HttpStatus.BAD_REQUEST
            responseBody = Response(false, e.message ?: "")
        } catch (e: Exception) {
            logger.warn(e.stackTrace.toString())
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(responseStatus)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PostMapping("/user/login")
    fun login(
            @RequestHeader headers: Map<String, String>,
            @RequestBody requestBody: LoginRequest
    ): ResponseEntity<*> {
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        var responseBody: Response? = null
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            val token = loginService.login(requestBody.username, requestBody.password)
            responseBody = LoginResponse(token)
        } catch (e: PasswordIncorrectException) {
            status = HttpStatus.UNAUTHORIZED
            responseBody = Response(false, "Password Incorrect")
        } catch (e: UsernameIncorrectException) {
            status = HttpStatus.UNAUTHORIZED
            responseBody = Response(false, "Username Incorrect")
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message ?: "")
        } catch (e: Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PostMapping("/user/register")
    fun register(
            @RequestHeader headers: Map<String, String>,
            @RequestBody request: RegisterRequest): ResponseEntity<*> {
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        var responseBody: Response? = null
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            userService.register(request.username, request.password, request.email)
        } catch (e: UsernameExistsException) {
            status = HttpStatus.UNAUTHORIZED
            responseBody = Response(false, "Username already exists")
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message ?: "")
        } catch (e: Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PostMapping
    fun logout(
            @RequestHeader headers: Map<String, String>
    ): ResponseEntity<*> {
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        var responseBody: Response? = null
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            loginService.logout(headers["token"] ?: error("token not found in header"))
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message ?: "")
        } catch (e: Exception) {
            logger.warn(e.stackTrace.toString())
            status = HttpStatus.INTERNAL_SERVER_ERROR
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }
}