package com.young_zy.forum.service

import com.young_zy.forum.common.ReactiveContextHolder
import com.young_zy.forum.common.exception.ForbiddenException
import com.young_zy.forum.model.RateLimit
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Service
class RateLimitService {
    @Autowired
    private lateinit var rateLimitRedisTemplate: RedisTemplate<String, RateLimit>

    /**
     * build rate limit response headers
     * @author young-zy
     */
    suspend fun buildHeader() {
        val requestHeaders = ReactiveContextHolder.request.map { it.headers }
        val rateLimit = hasReserve(
            requestHeaders.awaitSingle().getFirst("X-Real-IP")
                ?: throw Exception("X-Real-IP in header not found.If you are maintainer, please check balance loader settings")
        )
        val responseHeaders = ReactiveContextHolder.response.map { it.headers }.awaitSingle()
        responseHeaders.add("X-RateLimit-Limit", "500")
        responseHeaders.add("X-RateLimit-Remaining", rateLimit.timesRemain.toString())
        responseHeaders.add("X-RateLimit-Reset", rateLimit.resetTimestamp.toString())
        if (rateLimit.timesRemain <= -1) {
            throw ForbiddenException("rate limit exceeded, please check the header ")
        }
    }

//    fun buildHeader2(): Mono<Tuple2<HttpHeaders, HttpHeaders>> {
//        val requestHeaders = ReactiveContextHolder.request.map {
//            it.headers
//        }
//        val responseHeaders = ReactiveContextHolder.response.map {
//            it.headers
//        }
//        return Mono.zip(requestHeaders, responseHeaders).flatMap {
//            val rateLimit = hasReserve(it.t1.getFirst("X-Real-IP")
//            ?: error(Exception("X-Real-IP in header not found.If you are maintainer, please check balance loader settings"))
//            )
//            it.t2.add("X-RateLimit-Limit", "500")
//            it.t2.add("X-RateLimit-Remaining", rateLimit.timesRemain.toString())
//            it.t2.add("X-RateLimit-Reset", rateLimit.resetTimestamp.toString())
//            if (rateLimit.timesRemain <= -1) {
//                error(RateLimitExceededException())
//            }
//            Mono.just(it)
//        }
//    }

    /**
     * check if user still has access rate
     *
     * @author young-zy
     * @param userIp userId of user
     * @return rate limit object. timesRemain will be -1 if rateLimitExceeded
     */
    @Transactional
    fun hasReserve(userIp: String): RateLimit {
        // check whether user info is not in the redis
        val temp = if (!rateLimitRedisTemplate.hasKey(userIp)) {
            RateLimit(userIp, 500, Timestamp(System.currentTimeMillis() + 30000))
        } else {
            rateLimitRedisTemplate.opsForValue().get(userIp)
        }
        return if (temp!!.resetTimestamp.before(Timestamp(System.currentTimeMillis()))) {
            temp.resetTimestamp = Timestamp(System.currentTimeMillis() + 300000)
            temp.timesRemain = 500 - 1
            rateLimitRedisTemplate.opsForValue().set(userIp, temp)
            temp
        } else {
            if (temp.timesRemain <= 0) {
                temp.timesRemain = -1
                temp
            } else {
                temp.timesRemain--
                rateLimitRedisTemplate.opsForValue().set(userIp, temp)
                temp
            }
        }
    }

}