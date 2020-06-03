package com.young_zy.forum.service


import com.young_zy.forum.model.thread.ThreadProjection
import com.young_zy.forum.repo.ThreadNativeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class HitRateService {
    @Autowired
    private lateinit var hitRateRedisTemplate: RedisTemplate<String, Int>

    @Autowired
    private lateinit var threadNativeRepository: ThreadNativeRepository

    fun increment(uid: Long, tid: Int) {
        if (check(uid, tid)) {
            if (hitRateRedisTemplate.hasKey("order")) {
                val current = LocalDateTime.now()
                var next = current.plusDays(1)
                next = next.withHour(3).withMinute(0).withSecond(0)
                val diff = Duration.between(current, next).toSeconds()
                hitRateRedisTemplate.opsForZSet().incrementScore("order", tid, 1.0)
                hitRateRedisTemplate.expire("order", diff, TimeUnit.SECONDS)
            } else {
                hitRateRedisTemplate.opsForZSet().incrementScore("order", tid, 1.0)
            }
        }
    }

    private fun check(uid: Long, tid: Int): Boolean {
        return if (hitRateRedisTemplate.opsForValue().get("$uid:$tid") === null) {
            hitRateRedisTemplate.opsForValue().set("$uid:$tid", 1, Duration.ofHours(12))
            true
        } else {
            false
        }
    }

    suspend fun getOrder(count: Int): List<ThreadProjection> {
        val threadIds = hitRateRedisTemplate.opsForZSet().reverseRange("order", 0, count.toLong())!!
        val res = mutableListOf<ThreadProjection>()
        threadIds.forEach flag@{
            val temp = threadNativeRepository.findByTid(it) ?: return@flag
            res.add(temp)
        }
        return res
    }
}