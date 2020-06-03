package com.young_zy.forum.controller.response

import com.young_zy.forum.model.thread.SearchResultDTO
import kotlinx.coroutines.flow.Flow

data class SearchResponse(val searches: Flow<SearchResultDTO>) : Response()