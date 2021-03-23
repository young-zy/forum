package com.young_zy.forum.common.exception

class RateLimitExceededException(override val message: String = "API request rate exceeded the limit") :
    RuntimeException()