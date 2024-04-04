package com.lgtm.android.data.model.response

import com.lgtm.android.data.model.mapper.parseDate
import com.lgtm.domain.mission_suggestion.SuggestionVO

data class SuggestionDTO(
    val suggestionId: Int?,
    val title: String?,
    val description: String?,
    val date: String?,
    val likeNum: String?,
    val isLiked: Boolean?,
    val isMyPost: Boolean?
) {
    fun toVO(): SuggestionVO {
        return SuggestionVO(
            suggestionId = requireNotNull(suggestionId),
            title = requireNotNull(title),
            description = requireNotNull(description),
            date = parseDate(date),
            likeNum = requireNotNull(likeNum),
            isLiked = requireNotNull(isLiked),
            isMyPost = requireNotNull(isMyPost)
        )
    }
}
