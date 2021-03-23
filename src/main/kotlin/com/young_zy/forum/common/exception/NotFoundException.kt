package com.young_zy.forum.common.exception

import org.springframework.http.HttpStatus

class NotFoundException(override val message: String = "Requested object not found") :
    BaseHttpException(HttpStatus.NOT_FOUND)