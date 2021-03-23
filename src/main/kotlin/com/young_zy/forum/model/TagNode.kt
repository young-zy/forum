package com.young_zy.forum.model

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node

@Node("Tag")
data class TagNode(
    @Id
    @GeneratedValue
    var id: Long?,
    var tagName: String
)