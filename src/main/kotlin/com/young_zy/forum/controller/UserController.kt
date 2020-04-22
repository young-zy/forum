package com.young_zy.forum.controller

import com.young_zy.forum.config.stackTraceString
import com.young_zy.forum.controller.request.*
import com.young_zy.forum.controller.response.LoginResponse
import com.young_zy.forum.controller.response.Response
import com.young_zy.forum.controller.response.UserResponse
import com.young_zy.forum.service.LoginService
import com.young_zy.forum.service.RateLimitService
import com.young_zy.forum.service.UserService
import com.young_zy.forum.service.exception.AuthException
import com.young_zy.forum.service.exception.NotAcceptableException
import com.young_zy.forum.service.exception.NotFoundException
import com.young_zy.forum.service.exception.RateLimitExceededException
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

    @GetMapping(path = ["/user/{userId}", "/user"])
    fun getUser(@PathVariable userId: Int?,
                @RequestHeader headers: Map<String, String>): ResponseEntity<*> {
        val responseHeaders = HttpHeaders()
        var responseStatus = HttpStatus.OK
        var responseBody: Response? = null
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            responseBody = UserResponse(userService.getDetailedUser(headers["token"] ?: "", userId))
        } catch (e: AuthException) {
            responseStatus = HttpStatus.UNAUTHORIZED
            responseBody = Response(false, e.message)
        } catch (e: RateLimitExceededException) {
            responseStatus = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: NotFoundException) {
            responseStatus = HttpStatus.NOT_FOUND
            responseBody = Response(false, e.message)
        } catch (e: Exception) {
            logger.error(e.stackTraceString)
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
            responseBody = Response(false, e.message)
        } catch (e: NotAcceptableException) {
            responseStatus = HttpStatus.NOT_ACCEPTABLE
            responseBody = Response(false, e.message)
        } catch (e: IllegalArgumentException) {
            responseStatus = HttpStatus.BAD_REQUEST
            responseBody = Response(false, e.message ?: "")
        } catch (e: Exception) {
            logger.error(e.stackTraceString)
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
        } catch (e: NotAcceptableException) {
            status = HttpStatus.UNAUTHORIZED
            responseBody = Response(false, e.message)
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: Exception) {
            logger.error(e.stackTraceString)
            status = HttpStatus.INTERNAL_SERVER_ERROR
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PostMapping("/user")
    fun register(
            @RequestHeader headers: Map<String, String>,
            @RequestBody request: RegisterRequest): ResponseEntity<*> {
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        var responseBody: Response? = null
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            userService.register(request.username, request.password, request.email)
        } catch (e: NotAcceptableException) {
            status = HttpStatus.NOT_ACCEPTABLE
            responseBody = Response(false, e.message)
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST
            responseBody = Response(false, e.message ?: "")
        } catch (e: Exception) {
            logger.error(e.stackTraceString)
            status = HttpStatus.INTERNAL_SERVER_ERROR
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PostMapping("/user/logout")
    fun logout(
            @RequestHeader headers: Map<String, String>
    ): ResponseEntity<*> {
        var status = HttpStatus.NO_CONTENT
        val responseHeaders = HttpHeaders()
        var responseBody: Response? = null
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            loginService.logout(headers["token"] ?: error("token not found in header"))
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: Exception) {
            logger.error(e.stackTraceString)
            status = HttpStatus.INTERNAL_SERVER_ERROR
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PutMapping("/user/giveSystemAdmin")
    fun giveSystemAdmin(
            @RequestHeader headers: Map<String, String>,
            @RequestBody requestBody: GiveSystemAdminRequest
    ): ResponseEntity<Response> {
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        var responseBody: Response? = null
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            userService.giveSystemAdmin(headers["token"] ?: "", requestBody.userIds)
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: AuthException) {
            status = HttpStatus.UNAUTHORIZED
            responseBody = Response(false, e.message)
        } catch (e: Exception) {
            logger.error(e.stackTraceString)
            status = HttpStatus.INTERNAL_SERVER_ERROR
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PutMapping("/user/giveSectionAdmin")
    fun giveSectionAdmin(
            @RequestHeader headers: Map<String, String>,
            @RequestBody requestBody: GiveSectionAdminRequest
    ): ResponseEntity<Response> {
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        var responseBody: Response? = null
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            userService.giveSectionAdmin(headers["token"] ?: "", requestBody.userIds, requestBody.sectionIds)
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: AuthException) {
            status = HttpStatus.UNAUTHORIZED
            responseBody = Response(false, e.message)
        } catch (e: Exception) {
            logger.error(e.stackTraceString)
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