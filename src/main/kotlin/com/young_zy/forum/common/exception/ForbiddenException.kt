package com.young_zy.forum.common.exception

import org.springframework.http.HttpStatus

/**
 * An exception thrown when user Auth is not legit to certain operations
 */
class ForbiddenException(override val message: String = "permission denied") : BaseHttpException(HttpStatus.FORBIDDEN)