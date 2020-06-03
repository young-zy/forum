//package com.young_zy.forum.repo
//
//import com.young_zy.forum.model.reply.ReplyEntity
//import com.young_zy.forum.model.reply.ReplyProjection
//import kotlinx.coroutines.flow.Flow
//import org.springframework.data.domain.Pageable
//import org.springframework.data.repository.kotlin.CoroutineCrudRepository
//
//
//interface ReplyRepository : CoroutineCrudRepository<ReplyEntity, Int> {
//    suspend fun findAllByTid(tid: Int, pageable: Pageable): Flow<ReplyProjection>
//
//    suspend fun findByRid(rid: Int): ReplyEntity?
//
//    suspend fun findProjectionByRid(rid: Int): ReplyProjection?
//
//    suspend fun deleteAllByTid(tid: Int)
//
//    suspend fun countByTid(tid: Int): Int
//}