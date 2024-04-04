package com.lgtm.android.mission_suggestion.ui.detail.presentation.contract

import com.lgtm.android.common_ui.util.UiState
import com.lgtm.domain.mission_suggestion.SuggestionVO
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface SuggestionDetailOutputs {
    val detailState: StateFlow<UiState<SuggestionVO>>
    val detailUiEffect: SharedFlow<SuggestionDetailUiEffect>
}