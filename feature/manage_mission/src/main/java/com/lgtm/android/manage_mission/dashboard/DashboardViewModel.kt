package com.lgtm.android.manage_mission.dashboard

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lgtm.android.common_ui.base.BaseViewModel
import com.lgtm.android.common_ui.model.DashboardUI
import com.lgtm.android.common_ui.model.MissionProcessInfoUI
import com.lgtm.android.common_ui.model.PingPongSeniorUI
import com.lgtm.android.common_ui.model.mapper.toUiModel
import com.lgtm.android.common_ui.util.NetworkState
import com.lgtm.domain.constants.ProcessState
import com.lgtm.domain.constants.Role
import com.lgtm.domain.entity.response.PingPongSeniorVO
import com.lgtm.domain.usecase.MissionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val missionUseCase: MissionUseCase
) : BaseViewModel() {

    private val _dashboardInfo = MutableLiveData<DashboardUI>()
    val dashboardInfo: LiveData<DashboardUI> = _dashboardInfo
    fun fetchDashboardInfo(missionId: Int) {
        viewModelScope.launch(lgtmErrorHandler) {
            missionUseCase.fetchDashboardInfo(missionId)
                .onSuccess {
                    _dashboardInfo.postValue(it.toUiModel())
                    Log.d(TAG, "fetchMissionInfo: $it")
                }.onFailure {
                    Log.e(TAG, "fetchMissionInfo: $it")
                }
        }
    }

    private val _pingPongSeniorVO = MutableLiveData<PingPongSeniorUI>()
    val pingPongSeniorUI: LiveData<PingPongSeniorUI> = _pingPongSeniorVO

    private val _pingPongSeniorState =
        MutableLiveData<NetworkState<PingPongSeniorVO>>(NetworkState.Init)
    val pingPongSeniorState: LiveData<NetworkState<PingPongSeniorVO>> = _pingPongSeniorState

    fun fetchSeniorMissionStatus(missionId: Int, juniorId: Int) {
        viewModelScope.launch(lgtmErrorHandler) {
            Log.d(TAG, "fetchSeniorMissionStatus - viewmodel: $missionId, $juniorId")
            missionUseCase.fetchSeniorMissionStatus(
                missionId = missionId,
                juniorId = juniorId
            ).onSuccess {
                _pingPongSeniorVO.postValue(it.toUiModel(getRole()))
                _pingPongSeniorState.postValue(NetworkState.Success(it))
                Log.d(TAG, "fetchPingPongSeniorVO: $it")
            }.onFailure {
                Log.e(TAG, "fetchPingPongSeniorVO: $it")
            }
        }
    }

    fun getRole() = Role.REVIEWER
    fun getMissionStatus(): ProcessState {
        return pingPongSeniorUI.value?.processState ?: throw IllegalStateException("status is null")
    }

    fun getMissionProcessInfo(): MissionProcessInfoUI {
        return pingPongSeniorUI.value?.missionProcessInfo
            ?: throw IllegalStateException("missionProcessInfo is null")
    }

    private val _confirmDepositStatus = MutableLiveData<NetworkState<Boolean>>(NetworkState.Init)
    val confirmDepositStatus: LiveData<NetworkState<Boolean>> = _confirmDepositStatus
    fun confirmDepositCompleted(missionId: Int, juniorId: Int) {
        viewModelScope.launch(lgtmErrorHandler) {
            missionUseCase.confirmDepositCompleted(
                missionId = missionId,
                juniorId = juniorId
            ).onSuccess {
                _confirmDepositStatus.postValue(NetworkState.Success(it))
                Log.d(TAG, "confirmDepositCompleted: $it")
            }.onFailure {
                Log.e(TAG, "confirmDepositCompleted: $it")
            }
        }

    }
}