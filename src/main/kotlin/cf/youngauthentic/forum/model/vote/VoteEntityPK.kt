package cf.youngauthentic.forum.model.vote

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Id

data class VoteEntityPK(
        @Column(name = "uid", nullable = false)
        @Id
        var uid: Int = 0,
        @Column(name = "rid", nullable = false, precision = 0)
        @Id
        var rid: Int = 0
) : Serializable