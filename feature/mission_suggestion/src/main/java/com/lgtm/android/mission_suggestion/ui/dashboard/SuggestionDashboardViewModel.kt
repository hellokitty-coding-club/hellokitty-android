package com.lgtm.android.mission_suggestion.ui.dashboard

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.lgtm.android.common_ui.base.BaseViewModel
import com.lgtm.android.common_ui.model.mapper.toUiModel
import com.lgtm.android.common_ui.util.UiState
import com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract.SuggestionDashboardInputs
import com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract.SuggestionDashboardOutputs
import com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract.SuggestionDashboardUiEffect
import com.lgtm.domain.constants.Role
import com.lgtm.domain.mission_suggestion.SuggestionContent
import com.lgtm.domain.mission_suggestion.SuggestionVO
import com.lgtm.domain.mission_suggestion.SuggestionViewType
import com.lgtm.domain.repository.AuthRepository
import com.lgtm.domain.usecase.SuggestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuggestionDashboardViewModel @Inject constructor(
    private val suggestionUseCase: SuggestionUseCase,
    authRepository: AuthRepository
): BaseViewModel(), SuggestionDashboardInputs, SuggestionDashboardOutputs {

    val createBtnVisibility: Boolean = authRepository.getMemberType() == Role.REVIEWEE

    private val _suggestionDashboardState = MutableStateFlow<UiState<List<SuggestionContent>>>(UiState.Init)
    override val suggestionDashboardState: StateFlow<UiState<List<SuggestionContent>>>
        get() = _suggestionDashboardState

    private val _suggestionDashboardUiEffect: MutableSharedFlow<SuggestionDashboardUiEffect> = MutableSharedFlow(replay = 0)
    override val suggestionDashboardUiEffect: SharedFlow<SuggestionDashboardUiEffect>
        get() = _suggestionDashboardUiEffect

    fun fetchSuggestionList() {
        viewModelScope.launch(lgtmErrorHandler) {
            suggestionUseCase.getSuggestionList()
                .onSuccess {
                    _suggestionDashboardState.value = UiState.Success(data = convertToUiModel(it))
                    Log.d(TAG, "getSuggestion: $it")
                }.onFailure {
                    Firebase.crashlytics.recordException(it)
                    Log.e(TAG, "getSuggestion: $it")
                }
        }
    }

    private fun convertToUiModel(suggestions: List<SuggestionContent>): List<SuggestionContent> {
        return suggestions.map {
            when(it.viewType) {
               SuggestionViewType.CONTENT -> (it as SuggestionVO).toUiModel()
               else -> it
            }
        }
    }

    override fun likeSuggestion() {
        TODO("Not yet implemented")
    }

    override fun cancelLikeSuggestion() {
        TODO("Not yet implemented")
    }

    override fun moveToCreateSuggestion() {
        viewModelScope.launch(lgtmErrorHandler) {
            _suggestionDashboardUiEffect.emit(SuggestionDashboardUiEffect.CreateSuggestion)
        }
    }

    override fun moveToSuggestionDetail(suggestionId: Int) {
        viewModelScope.launch(lgtmErrorHandler) {
            _suggestionDashboardUiEffect.emit(SuggestionDashboardUiEffect.SuggestionDetail(suggestionId))
        }
    }

    override fun goBack() {
        viewModelScope.launch(lgtmErrorHandler) {
            _suggestionDashboardUiEffect.emit(SuggestionDashboardUiEffect.GoBack)
        }
    }

}