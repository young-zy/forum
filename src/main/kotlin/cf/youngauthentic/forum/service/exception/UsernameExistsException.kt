package cf.youngauthentic.forum.service.exception

class UsernameExistsException(override val message: String? = "Username already exists") : Exception()