package com.young_zy.forum.controller.response

import com.young_zy.forum.model.user.DetailedUser

class UserListResponse(val users: List<DetailedUser>) : Response()