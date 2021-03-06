package com.young_zy.forum.model.thread

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("thread")
data class ThreadEntity(
        @Id
        @Column("tid")
        var tid: Long? = null,
        @Column("sid")
        var sid: Long = 0,
        @Column("uid")
        var uid: Long = 0,
        @Column("title")
        var title: String = "",
        @Column("lastReplyTime")
        var lastReplyTime: LocalDateTime = LocalDateTime.now(),
        @Column("lastReplyUid")
        var lastReplyUid: Long = 0,
        @Column("hasBestAnswer")
        var hasBestAnswer: Boolean = false,
        @Column("postTime")
        var postTime: LocalDateTime = LocalDateTime.now(),
        @Column("question")
        var question: Boolean
)