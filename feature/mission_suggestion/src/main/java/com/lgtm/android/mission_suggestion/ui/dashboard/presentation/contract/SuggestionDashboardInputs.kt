package com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract

interface SuggestionDashboardInputs {
    fun likeSuggestion()
    fun cancelLikeSuggestion()
    fun moveToCreateSuggestion()
    fun moveToSuggestionDetail(suggestionId: Int)
    fun goBack()
}