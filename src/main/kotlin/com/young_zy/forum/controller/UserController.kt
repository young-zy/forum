package com.young_zy.forum.controller

import com.young_zy.forum.controller.request.*
import com.young_zy.forum.controller.response.LoginResponse
import com.young_zy.forum.controller.response.Response
import com.young_zy.forum.controller.response.UserListResponse
import com.young_zy.forum.controller.response.UserResponse
import com.young_zy.forum.service.LoginService
import com.young_zy.forum.service.RateLimitService
import com.young_zy.forum.service.UserService
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

    @GetMapping("/user/all")
    suspend fun getAllUser(@RequestParam page: Int?, @RequestParam size: Int?): UserListResponse {
        return UserListResponse(userService.getAllUser(page ?: 1, size ?: 10))
    }

    @GetMapping(path = ["/user/{userId}", "/user"])
    suspend fun getUser(@PathVariable userId: Long?,
                        @RequestHeader headers: Map<String, String>): ResponseEntity<*> {
        val responseHeaders = HttpHeaders()
        var responseStatus = HttpStatus.OK
        var responseBody: Response? = null
        rateLimitService.buildHeader(headers, responseHeaders)
        responseBody = UserResponse(userService.getDetailedUser(headers["token"] ?: "", userId))
        return ResponseEntity
                .status(responseStatus)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @PutMapping("/user")
    suspend fun userUpdate(
            @RequestHeader headers: Map<String, String>,
            @RequestBody body: UserUpdateRequest
    ): ResponseEntity<*> {
        val responseHeaders = HttpHeaders()
        var responseStatus = HttpStatus.OK
        var responseBody: Response? = null
        rateLimitService.buildHeader(headers, responseHeaders)
        userService.userInfoUpdate(
                headers["token"] ?: error("token not found in header"),
                body.password,
                body.newPassword,
                body.username,
                body.email)
        return ResponseEntity
                .status(responseStatus)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @PostMapping("/user/login")
    suspend fun login(
            @RequestHeader headers: Map<String, String>,
            @RequestBody requestBody: LoginRequest
    ): ResponseEntity<*> {
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        var responseBody: Response? = null
        rateLimitService.buildHeader(headers, responseHeaders)
        val token = loginService.login(requestBody.username, requestBody.password)
        responseBody = LoginResponse(token)
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @PostMapping("/user")
    suspend fun register(
            @RequestHeader headers: Map<String, String>,
            @RequestBody request: RegisterRequest): ResponseEntity<*> {
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        var responseBody: Response? = null
        rateLimitService.buildHeader(headers, responseHeaders)
        userService.register(request.username, request.password, request.email)
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @PostMapping("/user/logout")
    fun logout(
            @RequestHeader headers: Map<String, String>
    ): ResponseEntity<*> {
        var status = HttpStatus.NO_CONTENT
        val responseHeaders = HttpHeaders()
        var responseBody: Response? = null
        rateLimitService.buildHeader(headers, responseHeaders)
        loginService.logout(headers["token"] ?: error("token not found in header"))
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @PutMapping("/user/giveSystemAdmin")
    suspend fun giveSystemAdmin(
            @RequestHeader headers: Map<String, String>,
            @RequestBody requestBody: GiveSystemAdminRequest
    ): ResponseEntity<Response> {
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        var responseBody: Response? = null
        rateLimitService.buildHeader(headers, responseHeaders)
        userService.giveSystemAdmin(headers["token"] ?: "", requestBody.userIds)
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @PutMapping("/user/giveSectionAdmin")
    suspend fun giveSectionAdmin(
            @RequestHeader headers: Map<String, String>,
            @RequestBody requestBody: GiveSectionAdminRequest
    ): ResponseEntity<Response> {
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        var responseBody: Response? = null
        rateLimitService.buildHeader(headers, responseHeaders)
        userService.giveSectionAdmin(headers["token"] ?: "", requestBody.userIds, requestBody.sectionIds)
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }
}