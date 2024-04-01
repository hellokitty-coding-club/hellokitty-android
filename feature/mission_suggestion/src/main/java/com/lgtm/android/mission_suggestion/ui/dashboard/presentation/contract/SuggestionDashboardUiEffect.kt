package com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract

import com.lgtm.domain.mission_suggestion.SuggestionContent

sealed class SuggestionDashboardUiEffect {
    object GoBack: SuggestionDashboardUiEffect()
    object CreateSuggestion: SuggestionDashboardUiEffect()
    data class SuggestionDetail(val suggestionId: Int): SuggestionDashboardUiEffect()
    data class ShotSuggestionClickLogging(val suggestionContent: SuggestionContent): SuggestionDashboardUiEffect()
}