package com.lgtm.android.main.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.lgtm.android.common_ui.base.BaseViewModel
import com.lgtm.domain.constants.Role
import com.lgtm.domain.constants.UNKNOWN
import com.lgtm.domain.entity.response.SduiItemVO
import com.lgtm.domain.repository.AuthRepository
import com.lgtm.domain.repository.LoggingRepository
import com.lgtm.domain.repository.NotificationRepository
import com.lgtm.domain.server_drive_ui.SectionSubItemVO
import com.lgtm.domain.server_drive_ui.SectionTitleVO
import com.lgtm.domain.usecase.MissionUseCase
import com.swm.logging.android.logging_scheme.SWMLoggingScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: MissionUseCase,
    private val notificationRepository: NotificationRepository,
    private val loggingRepository: LoggingRepository,
    authRepository: AuthRepository
) : BaseViewModel() {

    private val role = authRepository.getMemberType()
    val fabVisibility = role == Role.REVIEWER

    private val _sduiList = MutableLiveData<List<SduiItemVO>>()
    val sduiList: LiveData<List<SduiItemVO>> = _sduiList

    fun getHomeInfo() {
        viewModelScope.launch(lgtmErrorHandler) {
            useCase.getHomeMission()
                .onSuccess {
                    _sduiList.postValue(it.contents)
                }.onFailure {
                    Firebase.crashlytics.recordException(it)
                    Log.e(TAG, "getHomeInfo: ${it.message}")
                }
        }
    }


    private val _hasNewNotification = MutableLiveData<Boolean>()
    val hasNewNotification: LiveData<Boolean> = _hasNewNotification

    fun hasNewNotification() {
        viewModelScope.launch(lgtmErrorHandler) {
            notificationRepository.hasNewNotification()
                .onSuccess {
                    _hasNewNotification.postValue(it)
                }.onFailure {
                    Firebase.crashlytics.recordException(it)
                    Log.e(TAG, "hasNewNotification: ${it.message}")
                }
        }
    }

    fun shotHomeMissionClickLogging(swmLoggingScheme: SWMLoggingScheme) {
        loggingRepository.shotSwmLogging(swmLoggingScheme)
    }

    fun getUserRole() = role

    fun shotFirstMissionClickLogging(swmLoggingScheme: SWMLoggingScheme) {
        loggingRepository.shotSwmLogging(swmLoggingScheme)
    }

    fun shotMissionSuggestionClickLogging(swmLoggingScheme: SWMLoggingScheme) {
        loggingRepository.shotSwmLogging(swmLoggingScheme)
    }

    fun getMissionSuggestionTitle(): String {
        val idx = getMissionSuggestionTitleIdx()
        return idx?.let {
            (_sduiList.value?.get(it)?.content as? SectionTitleVO)?.title ?: UNKNOWN
        } ?: UNKNOWN
    }

    private fun getMissionSuggestionTitleIdx(): Int? {
        var titleIdx: Int? = null
        _sduiList.value?.let {
            for(i in it.indices) {
                if (it[i].content is SectionSubItemVO) {
                    titleIdx = i-1
                    break
                }
            }
        }
        return titleIdx
    }
}