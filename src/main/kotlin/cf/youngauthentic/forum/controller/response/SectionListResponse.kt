package cf.youngauthentic.forum.controller.response

import cf.youngauthentic.forum.model.section.SectionEntity

data class SectionListResponse(
        val sections: List<SectionEntity>
) : Response()