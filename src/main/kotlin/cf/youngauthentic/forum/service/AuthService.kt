package cf.youngauthentic.forum.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuthService {
    @Autowired
    lateinit var loginService: LoginService

    fun hasAuth(token: String, config: AuthConfig) {
        val auth = loginService.getAuth(token)

    }
}