package com.young_zy.forum.service

import com.young_zy.forum.model.TagNode
import com.young_zy.forum.model.thread.ThreadEntity
import com.young_zy.forum.model.thread.ThreadNode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.neo4j.driver.internal.InternalNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.neo4j.core.ReactiveNeo4jClient
import org.springframework.data.neo4j.core.awaitFirstOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TagService {

    @Autowired
    private lateinit var neo4jClient: ReactiveNeo4jClient

    @Transactional
    suspend fun addRelation(thread: ThreadEntity, tags: List<String>) {
        tags.forEach {
            val query = "MERGE ( t:Thread { tid: \$tid, title: \$title, threadContent: \$threadContent } )" +
                    " -[r:HAS_TAG]- " +
                    "( tag: Tag { tagName: \$tagName } )"
            neo4jClient.query(query)
                .bind(thread.tid).to("tid")
                .bind(thread.title).to("title")
                .bind(thread.threadContent).to("threadContent")
                .bind(it).to("tagName")
                .fetch()
                .awaitFirstOrNull()
        }
    }

    @Transactional
    suspend fun updateRelation(tid: Long, tags: List<String>) {
        // delete original relations
        var query = "MATCH (t:Thread) - [r:HAS_TAG] - (tag:Tag)" +
                "WHERE t.tid = \$tid " +
                "DELETE r"
        // execute query
        neo4jClient.query(query)
            .bind(tid).to("tid")
            .fetch()
            .awaitFirstOrNull()

        // add new relations
        query = "MATCH (thread:Thread) WHERE t.tid = \$tid" +
                "MERGE (thread)-[r:HAS_TAG]-(tag:Tag{tagName: \$tagName})"
        tags.forEach {
            neo4jClient.query(query)
                .bind(tid).to("tid")
                .bind(it).to("tagName")
                .fetch()
                .awaitFirstOrNull()
        }
    }

    fun getTags(threadId: Long): Flow<TagNode> {
        val query = "MATCH (t:Thread{tid: \$tid })-[r:HAS_TAG]-(tag:Tag) RETURN tag"
        return neo4jClient.query(query)
            .bind(threadId).to("tid")
            .fetch()
            .all()
            .map {
                val value = it["tag"] as InternalNode
                TagNode(
                    value.id(),
                    value["tagName"].asString()
                )
            }.asFlow()
    }

    fun getThreads(tagId: Long, page: Int, size: Int): Flow<ThreadNode> {
        return neo4jClient.query("MATCH (tag:Tag)-[r:HAS_TAG]-(t:Thread) WHERE id(tag)=\$id RETURN t ORDER BY t.tid DESC SKIP \$skip LIMIT \$limit;")
            .bind(tagId).to("id")
            .bind((page - 1) * size).to("skip")
            .bind(size).to("limit")
            .fetch()
            .all()
            .map {
                val value = it["t"] as InternalNode
                ThreadNode(
                    value.id(),
                    value["tid"].asLong(),
                    value["title"].asString(),
                    value["threadContent"].asString()
                )
            }.asFlow()
    }

}