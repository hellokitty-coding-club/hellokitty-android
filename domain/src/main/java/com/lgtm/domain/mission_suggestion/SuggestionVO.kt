package com.lgtm.domain.mission_suggestion

import java.time.LocalDateTime

data class SuggestionVO(
    override val viewType: SuggestionViewType = SuggestionViewType.CONTENT,
    val title: String,
    val description: String,
    val suggestionId: Int,
    val date: LocalDateTime?,
    val likeNum: String,
    val isLiked: Boolean,
    val isMyPost: Boolean
): SuggestionContent