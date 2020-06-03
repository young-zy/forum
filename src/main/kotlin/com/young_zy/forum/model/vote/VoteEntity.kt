package com.young_zy.forum.model.vote

import org.springframework.data.relational.core.mapping.Column


data class VoteEntity(
        @Column("uid")
        var uid: Long,
        @Column("rid")
        var rid: Int,
        @Column("vote")
        var vote: Int
)