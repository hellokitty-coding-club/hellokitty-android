package com.lgtm.android.mission_suggestion.ui.create_suggestion

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.lgtm.android.common_ui.base.BaseViewModel
import com.lgtm.android.common_ui.constant.InfoType
import com.lgtm.android.common_ui.model.EditTextData
import com.lgtm.android.common_ui.util.UiState
import com.lgtm.domain.entity.request.CreateSuggestionRequestVO
import com.lgtm.domain.entity.response.CreateSuggestionResponseVO
import com.lgtm.domain.repository.SuggestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateSuggestionViewModel @Inject constructor(
    private val suggestionRepository: SuggestionRepository
): BaseViewModel() {

    /** title **/
    private val _suggestionTitleTextData = MutableStateFlow(
        EditTextData(
            text = MutableLiveData(""),
            infoStatus = MutableLiveData(InfoType.NONE),
            maxLength = 100,
            hint = "제목을 입력하세요."
        )
    )
    val suggestionTitleTextData: StateFlow<EditTextData> = _suggestionTitleTextData

    fun updateSuggestionTitleTextData() {
        _suggestionTitleTextData.value.infoStatus.value =
            if ((this._suggestionTitleTextData.value.text.value ?: "").isBlank()) {
                InfoType.TITLE_REQUIRED
            } else {
                InfoType.NONE
            }
        setIsSuggestionValid()
        setIsSuggestionEmpty()
    }

    /** content **/
    private val _suggestionContentTextData = MutableStateFlow(
        EditTextData(
            text = MutableLiveData(""),
            maxLength = 1000,
            hint = "본문을 입력하세요.",
            infoStatus = MutableLiveData(InfoType.NONE),
            wordCountVisibility = false
        )
    )
    val suggestionContentTextData: StateFlow<EditTextData> = _suggestionContentTextData

    fun updateSuggestionContentTextData() {
        _suggestionContentTextData.value.infoStatus.value =
            if ((this._suggestionContentTextData.value.text.value ?: "").isBlank()) {
                InfoType.CONTENT_REQUIRED
            } else {
                InfoType.NONE
            }
        setIsSuggestionValid()
        setIsSuggestionEmpty()
    }

    /** check suggestion valid **/
    private val _isSuggestionValid = MutableStateFlow(false)
    val isSuggestionValid: StateFlow<Boolean> =_isSuggestionValid

    private fun setIsSuggestionValid() {
        _isSuggestionValid.value =
            (_suggestionTitleTextData.value.infoStatus.value == InfoType.NONE)
                    && (_suggestionContentTextData.value.infoStatus.value == InfoType.NONE)
                    && (_suggestionTitleTextData.value.text.value?.isNotBlank() == true)
                    && (_suggestionContentTextData.value.text.value?.isNotBlank() == true)
    }

    private val _isSuggestionEmpty = MutableStateFlow(true)
    val isSuggestionEmpty: StateFlow<Boolean> = _isSuggestionEmpty

    private fun setIsSuggestionEmpty() {
        _isSuggestionEmpty.value =
            (_suggestionTitleTextData.value.text.value.isNullOrBlank() && _suggestionContentTextData.value.text.value.isNullOrBlank())
    }

    /** create suggestion **/
    private val _createSuggestionState: MutableStateFlow<UiState<CreateSuggestionResponseVO>> = MutableStateFlow(UiState.Init)
    val createSuggestionState: StateFlow<UiState<CreateSuggestionResponseVO>>
        get() = _createSuggestionState

    fun createSuggestion() {
        viewModelScope.launch(lgtmErrorHandler) {
            suggestionRepository.createSuggestion(createSuggestionVO())
                .onSuccess {
                    _createSuggestionState.value = UiState.Success(data = it)
                    Log.d(TAG, "createSuggestion: $it")
                }.onFailure {
                    _createSuggestionState.value = UiState.Failure(msg = it.message)
                    Firebase.crashlytics.recordException(it)
                    Log.e(TAG, "createSuggestion: $it")
                }
        }
    }

    private fun createSuggestionVO() = CreateSuggestionRequestVO(
        title = requireNotNull(suggestionTitleTextData.value.text.value),
        description = requireNotNull(suggestionContentTextData.value.text.value)
    )
}