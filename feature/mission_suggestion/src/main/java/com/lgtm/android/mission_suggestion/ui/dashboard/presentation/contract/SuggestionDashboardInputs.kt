package com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract

import com.lgtm.domain.mission_suggestion.SuggestionVO

interface SuggestionDashboardInputs {
    fun likeSuggestion(index: Int, suggestionId: Int)
    fun cancelLikeSuggestion(index: Int, suggestionId: Int)
    fun moveToCreateSuggestion()
    fun moveToSuggestionDetail(suggestionId: Int, suggestionVO: SuggestionVO)
    fun goBack()
}