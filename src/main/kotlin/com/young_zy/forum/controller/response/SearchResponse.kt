package com.young_zy.forum.controller.response

import com.young_zy.forum.model.thread.SearchResultDTO

data class SearchResponse(val searches: List<SearchResultDTO>) : Response()