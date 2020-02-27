package cf.youngauthentic.forum.service.exception

class RateLimitExceededException(override val message: String? = "") : Exception()