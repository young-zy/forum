package com.young_zy.forum.common.exception

import org.springframework.http.HttpStatus

abstract class BaseHttpException(
    var statusCode: HttpStatus,
    override val message: String = "",
) : RuntimeException()