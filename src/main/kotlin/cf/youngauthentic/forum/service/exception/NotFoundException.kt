package cf.youngauthentic.forum.service.exception

class NotFoundException(override val message: String = "Requested object not found") : Exception()