package com.young_zy.forum.service.exception

/**
 * An exception thrown when user Auth is not legit to certain operations
 */
class AuthException(override val message: String = "") : Exception()