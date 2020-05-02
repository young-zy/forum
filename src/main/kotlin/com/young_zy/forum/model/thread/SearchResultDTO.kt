package com.young_zy.forum.model.thread

import java.math.BigInteger
import java.sql.Timestamp
import javax.persistence.ColumnResult
import javax.persistence.ConstructorResult
import javax.persistence.SqlResultSetMapping


@SqlResultSetMapping(
        name = "searchResultDTO",
        classes = [
            ConstructorResult(
                    targetClass = SearchResultDTO::class,
                    columns = [
                        ColumnResult(name = "tid", type = Int::class),
                        ColumnResult(name = "title", type = String::class),
                        ColumnResult(name = "lastReplyTime", type = Timestamp::class),
                        ColumnResult(name = "postTime", type = Timestamp::class),
                        ColumnResult(name = "uid", type = Int::class),
                        ColumnResult(name = "username", type = String::class),
                        ColumnResult(name = "question", type = Boolean::class),
                        ColumnResult(name = "hasBestAnswer", type = Boolean::class)
                    ]
            )
        ]
)
class SearchResultDTO(objects: Array<Any>) {
    var tid: BigInteger = objects[0] as BigInteger
    var title: String = objects[1] as String
    var lastReplyTime: Timestamp = objects[2] as Timestamp
    var postTime: Timestamp = objects[3] as Timestamp
    var uid: Int = objects[4] as Int
    var username: String = objects[5] as String
    var question: Boolean = (objects[6] as Byte).toInt() != 0
    var hasBestAnswer: Boolean = (objects[7] as Byte).toInt() != 0
}

