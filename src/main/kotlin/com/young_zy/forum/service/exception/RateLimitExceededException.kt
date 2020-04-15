package com.young_zy.forum.service.exception

class RateLimitExceededException(override val message: String = "API request rate exceeded the limit") : Exception()