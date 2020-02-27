package cf.youngauthentic.forum.model

import javax.persistence.*

@Entity
@Table(name = "Section", schema = "Forum")
data class SectionEntity(
        @Id
        @Column(name = "sid", nullable = false)
        var sid: Int = 0,
        @Basic
        @Column(name = "section_name", nullable = true, length = 45)
        var sectionName: String? = null

)