package com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract

interface SuggestionDashboardInputs {
    fun likeSuggestion(index: Int, suggestionId: Int)
    fun cancelLikeSuggestion(index: Int, suggestionId: Int)
    fun moveToCreateSuggestion()
    fun moveToSuggestionDetail(suggestionId: Int)
    fun goBack()
}