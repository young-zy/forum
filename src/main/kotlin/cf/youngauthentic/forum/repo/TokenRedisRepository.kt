package cf.youngauthentic.forum.repo

import cf.youngauthentic.forum.model.Token
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRedisRepository : CrudRepository<Token, String> {
    fun findByToken(token: String): Token
    fun deleteByToken(token: String)
}