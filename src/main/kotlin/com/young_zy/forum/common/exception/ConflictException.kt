package com.young_zy.forum.common.exception

import org.springframework.http.HttpStatus

class ConflictException(override val message: String = "") : BaseHttpException(HttpStatus.CONFLICT)