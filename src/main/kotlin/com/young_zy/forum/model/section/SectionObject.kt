package com.young_zy.forum.model.section

import com.young_zy.forum.model.thread.ThreadInListProjection

class SectionObject(sectionEntity: SectionEntity,
                    val threads: List<ThreadInListProjection>,
                    val currentPage: Int,
                    val totalPage: Int) {
    val sectionId = sectionEntity.sid
    val sectionName = sectionEntity.sectionName
}