package com.young_zy.forum.controller

import com.young_zy.forum.common.annotation.RateLimit
import com.young_zy.forum.controller.request.*
import com.young_zy.forum.controller.response.LoginResponse
import com.young_zy.forum.controller.response.Response
import com.young_zy.forum.controller.response.UserListResponse
import com.young_zy.forum.controller.response.UserResponse
import com.young_zy.forum.service.LoginService
import com.young_zy.forum.service.RateLimitService
import com.young_zy.forum.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@CrossOrigin
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
    suspend fun getUser(@PathVariable userId: Long?): Response? {
        var responseStatus = HttpStatus.OK
        var responseBody: Response?
        rateLimitService.buildHeader()
        responseBody = UserResponse(userService.getDetailedUser(userId))
        return responseBody
    }

    @PutMapping("/user")
    suspend fun userUpdate(
        @RequestBody body: UserUpdateRequest
    ): Response? {
        var responseStatus = HttpStatus.OK
        var responseBody: Response? = null
        rateLimitService.buildHeader()
        userService.userInfoUpdate(
            body.password,
            body.newPassword,
            body.username,
            body.email
        )
        return responseBody
    }

    @PostMapping("/user/login")
    suspend fun login(@RequestBody requestBody: LoginRequest): Response? {
        val responseBody: Response?
        rateLimitService.buildHeader()
        val token = loginService.login(requestBody.username, requestBody.password)
        responseBody = LoginResponse(token)
        return responseBody
    }

    @PostMapping("/user")
    suspend fun register(
        @RequestBody request: RegisterRequest
    ): Response? {
        val responseBody: Response? = null
        rateLimitService.buildHeader()
        userService.register(request.username, request.password, request.email)
        return responseBody
    }

    @PostMapping("/user/logout")
    suspend fun logout(): Response? {
        var responseBody: Response? = null
        rateLimitService.buildHeader()
        loginService.logout()
        return responseBody
    }

    @PutMapping("/user/giveSystemAdmin")
    suspend fun giveSystemAdmin(
        @RequestBody requestBody: GiveSystemAdminRequest
    ): Response? {
        var responseBody: Response? = null
        rateLimitService.buildHeader()
        userService.giveSystemAdmin(requestBody.userIds)
        return responseBody
    }

    @PutMapping("/user/giveSectionAdmin")
    suspend fun giveSectionAdmin(
        @RequestBody requestBody: GiveSectionAdminRequest
    ): Response? {
        var responseBody: Response? = null
        rateLimitService.buildHeader()
        userService.giveSectionAdmin(requestBody.userIds, requestBody.sectionIds)
        return responseBody
    }

    @GetMapping("/user/{userId}/recentThreads")
    fun getRecentThreads(@PathVariable userId: Long) {

    }
}