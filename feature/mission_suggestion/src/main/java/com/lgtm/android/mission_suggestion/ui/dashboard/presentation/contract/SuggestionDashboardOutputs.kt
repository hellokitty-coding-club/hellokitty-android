package com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract

import com.lgtm.android.common_ui.util.UiState
import com.lgtm.domain.mission_suggestion.SuggestionContent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface SuggestionDashboardOutputs {
    val suggestionDashboardState: StateFlow<UiState<List<SuggestionContent>>>
    val suggestionDashboardUiEffect: SharedFlow<SuggestionDashboardUiEffect>
}