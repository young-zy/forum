package com.young_zy.forum.model.section

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table


@Table("section")
data class SectionEntity(
        @Id
        @Column("sid")
        var sid: Long = 0,
        @Column("section_name")
        var sectionName: String? = null
)