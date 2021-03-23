package com.young_zy.forum.model.thread

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node

@Node("thread")
data class ThreadNode(
    @Id
    @GeneratedValue
    var id: Long?,
    var tid: Long,
    var title: String,
    var threadContent: String
)