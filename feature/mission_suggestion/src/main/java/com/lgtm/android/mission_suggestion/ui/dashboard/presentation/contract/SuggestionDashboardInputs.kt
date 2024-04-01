package com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract

import com.lgtm.android.common_ui.model.SuggestionUI

interface SuggestionDashboardInputs {
    fun likeSuggestion(index: Int, suggestionId: Int)
    fun cancelLikeSuggestion(index: Int, suggestionId: Int)
    fun moveToCreateSuggestion()
    fun moveToSuggestionDetail(suggestionId: Int, suggestionUI: SuggestionUI)
    fun goBack()
}