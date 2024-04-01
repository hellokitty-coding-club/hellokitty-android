package com.lgtm.android.mission_suggestion.ui.dashboard

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.lgtm.android.common_ui.base.BaseViewModel
import com.lgtm.android.common_ui.model.SuggestionUI
import com.lgtm.android.common_ui.model.mapper.toUiModel
import com.lgtm.android.common_ui.model.mapper.toVOModel
import com.lgtm.android.common_ui.util.UiState
import com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract.SuggestionDashboardInputs
import com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract.SuggestionDashboardOutputs
import com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract.SuggestionDashboardUiEffect
import com.lgtm.domain.constants.Role
import com.lgtm.domain.mission_suggestion.SuggestionContent
import com.lgtm.domain.mission_suggestion.SuggestionVO
import com.lgtm.domain.mission_suggestion.SuggestionViewType
import com.lgtm.domain.repository.AuthRepository
import com.lgtm.domain.repository.LoggingRepository
import com.lgtm.domain.repository.SuggestionRepository
import com.lgtm.domain.usecase.SuggestionUseCase
import com.swm.logging.android.logging_scheme.SWMLoggingScheme
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
    private val suggestionRepository: SuggestionRepository,
    private val loggingRepository: LoggingRepository,
    authRepository: AuthRepository
): BaseViewModel(), SuggestionDashboardInputs, SuggestionDashboardOutputs {

    val createBtnVisibility: Boolean = authRepository.getMemberType() == Role.REVIEWEE

    private val _suggestionDashboardState = MutableStateFlow<UiState<List<SuggestionContent>>>(UiState.Init)
    override val suggestionDashboardState: StateFlow<UiState<List<SuggestionContent>>>
        get() = _suggestionDashboardState

    private val _suggestionDashboardUiEffect: MutableSharedFlow<SuggestionDashboardUiEffect> = MutableSharedFlow(replay = 0)
    override val suggestionDashboardUiEffect: SharedFlow<SuggestionDashboardUiEffect>
        get() = _suggestionDashboardUiEffect

    /* 미션 제안 fetch 기능 */
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

    /* 미션 제안 좋아요 기능 */

    override fun likeSuggestion(index: Int, suggestionId: Int) {
        viewModelScope.launch(lgtmErrorHandler) {
            suggestionRepository.likeSuggestion(suggestionId)
                .onSuccess {
                    updateLikeState(index, it.likeNum, it.isLiked)
                    Log.d(TAG, "likeSuggestion: $it")
                }.onFailure {
                    Firebase.crashlytics.recordException(it)
                    Log.e(TAG, "likeSuggestion: $it")
                }
        }
    }

    override fun cancelLikeSuggestion(index: Int, suggestionId: Int) {
        viewModelScope.launch(lgtmErrorHandler) {
            suggestionRepository.cancelLikeSuggestion(suggestionId)
                .onSuccess {
                    updateLikeState(index, it.likeNum, it.isLiked)
                    Log.d(TAG, "cancelLikeSuggestion: $it")
                }.onFailure {
                    Firebase.crashlytics.recordException(it)
                    Log.e(TAG, "cancelLikeSuggestion: $it")
                }
        }
    }

    private fun updateLikeState(index: Int, likeNum: String, isLike: Boolean) {
        if (suggestionDashboardState.value is UiState.Success) {
            val suggestions = (suggestionDashboardState.value as UiState.Success).data.toMutableList()
            val suggestion = suggestions[index] as SuggestionUI
            suggestions[index] = suggestion.copy(
                likeNum = likeNum,
                isLiked = isLike
            )
            _suggestionDashboardState.value = UiState.Success(data = suggestions)
        }
    }

    /* uiEffect 핸들 */

    override fun moveToCreateSuggestion() {
        viewModelScope.launch(lgtmErrorHandler) {
            _suggestionDashboardUiEffect.emit(SuggestionDashboardUiEffect.CreateSuggestion)
        }
    }

    override fun moveToSuggestionDetail(suggestionId: Int, suggestionUI: SuggestionUI) {
        val suggestionContent = suggestionUI.toVOModel()
        viewModelScope.launch(lgtmErrorHandler) {
            _suggestionDashboardUiEffect.emit(SuggestionDashboardUiEffect.SuggestionDetail(suggestionId))
            _suggestionDashboardUiEffect.emit(SuggestionDashboardUiEffect.ShotSuggestionClickLogging(suggestionContent))
        }
    }

    override fun goBack() {
        viewModelScope.launch(lgtmErrorHandler) {
            _suggestionDashboardUiEffect.emit(SuggestionDashboardUiEffect.GoBack)
        }
    }

    fun shotSwmLogging(swmLoggingScheme: SWMLoggingScheme) {
        loggingRepository.shotSwmLogging(swmLoggingScheme)
    }
}