package com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract

sealed class SuggestionDashboardUiEffect {
    object GoBack: SuggestionDashboardUiEffect()
    object CreateSuggestion: SuggestionDashboardUiEffect()
    data class SuggestionDetail(val suggestionId: Int): SuggestionDashboardUiEffect()
}