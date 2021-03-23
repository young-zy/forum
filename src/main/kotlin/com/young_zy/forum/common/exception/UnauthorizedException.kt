package com.young_zy.forum.common.exception

import org.springframework.http.HttpStatus

class UnauthorizedException(override val message: String = "") : BaseHttpException(HttpStatus.UNAUTHORIZED)