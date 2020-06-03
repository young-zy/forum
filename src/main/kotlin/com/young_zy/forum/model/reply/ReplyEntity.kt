package com.young_zy.forum.model.reply

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("reply")
data class ReplyEntity(
        @Column("rid")
        @Id
        var rid: Int? = null,
        @Column("tid")
        var tid: Int = 0,
        @Column("replyContent")
        var replyContent: String = "",
        @Column("uid")
        var uid: Long,
        @Column("replyTime")
        var replyTime: LocalDateTime = LocalDateTime.now(),
        @Column("lastEditTime")
        var lastEditTime: LocalDateTime = LocalDateTime.now(),
        @Column("priority")
        var priority: Double = 0.0,
        @Column("isBestAnswer")
        var bestAnswer: Boolean = false,
        @Column("upVote")
        var upVote: Int = 0,
        @Column("downVote")
        var downVote: Int = 0
//        ,
//        @JoinColumn(name = "tid", referencedColumnName = "tid", nullable = false, insertable = false, updatable = false)
//        @ManyToOne
//        var threadByTid: ThreadEntity? = null,
//        @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false, insertable = false, updatable = false)
//        @ManyToOne
//        var userByUid: UserEntity? = null
)