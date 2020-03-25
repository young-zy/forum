package cf.youngauthentic.forum.model.section

import cf.youngauthentic.forum.model.thread.ThreadInListProjection

class SectionObject(sectionEntity: SectionEntity,
                    val threads: List<ThreadInListProjection>,
                    val currentPage: Int,
                    val totalPage: Int) {
    init {
        val sectionId = sectionEntity.sid
        val sectionName = sectionEntity.sectionName
    }
}