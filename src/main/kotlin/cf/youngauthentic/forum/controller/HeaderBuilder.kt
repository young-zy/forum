package cf.youngauthentic.forum.controller

import org.springframework.http.HttpHeaders

class HeaderBuilder {
    fun buildHeader(): HttpHeaders {
        val headers = HttpHeaders()
        headers.add("X-RateLimit-Limit", null)
        headers.add("X-RateLimit-Remaining", null)
        headers.add("X-RateLimit-Reset", null)
        return headers
    }
}