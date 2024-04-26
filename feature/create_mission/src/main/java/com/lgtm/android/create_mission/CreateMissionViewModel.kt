package com.lgtm.android.create_mission

import android.app.DatePickerDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lgtm.android.common_ui.base.BaseViewModel
import com.lgtm.android.common_ui.constant.InfoType
import com.lgtm.android.common_ui.model.EditTextData
import com.lgtm.android.common_ui.util.NetworkState
import com.lgtm.domain.entity.LgtmResponseException
import com.lgtm.domain.entity.request.PostMissionRequestDTO
import com.lgtm.domain.entity.response.PostMissionResponseVO
import com.lgtm.domain.repository.MissionRepository
import com.lgtm.domain.util.isoStyleFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class CreateMissionViewModel @Inject constructor(
    private val missionRepository: MissionRepository
) : BaseViewModel() {

    private fun isConsistOnlyWithSpace(liveData: LiveData<String>) =
        (liveData.value?.isBlank() == true && liveData.value?.isNotEmpty() == true)


    /** step 1 */

    val titleEditTextData = MutableLiveData(
        EditTextData(
            text = MutableLiveData(""),
            infoStatus = MutableLiveData(InfoType.NONE),
            maxLength = 100,
            hint = "제목을 입력하세요."
        )
    )

    val repoUrlEditTextData = MutableLiveData(
        EditTextData(
            text = MutableLiveData(""),
            infoStatus = MutableLiveData(InfoType.NONE),
            maxLength = 5000,
            hint = "Github URL을 입력하세요.",
            wordCountVisibility = false
        )
    )

    val title: LiveData<String> =
        titleEditTextData.value?.text ?: MutableLiveData("")

    fun updateMissionTitleInfoStatus() {
        if (isConsistOnlyWithSpace(title))
            titleEditTextData.value?.infoStatus?.value = InfoType.SPACE_ONLY_NOT_ALLOWED
        else
            titleEditTextData.value?.infoStatus?.value = InfoType.NONE
    }

    private fun isTitleValid(): Boolean {
        return titleEditTextData.value?.infoStatus?.value == InfoType.NONE
                && titleEditTextData.value?.text?.value?.isNotBlank() == true
    }

    val repositoryUrl: LiveData<String> =
        repoUrlEditTextData.value?.text ?: MutableLiveData("")

    fun updateMissionRepoUrlInfoStatus() {
        if (isConsistOnlyWithSpace(repositoryUrl))
            repoUrlEditTextData.value?.infoStatus?.value = InfoType.SPACE_ONLY_NOT_ALLOWED
        else if (repositoryUrl.value?.isNotBlank() == true && !isGithubUrl(
                repositoryUrl.value ?: ""
            )
        ) repoUrlEditTextData.value?.infoStatus?.value = InfoType.GITHUB_URL_ONLY
        else repoUrlEditTextData.value?.infoStatus?.value = InfoType.NONE
    }

    private fun isMissionRepoUrlValid(): Boolean {
        return repoUrlEditTextData.value?.infoStatus?.value == InfoType.NONE
    }

    private fun isGithubUrl(url: String): Boolean {
        // todo webview에서 https 없이도 열리는지 확인하고 https 없이도 열리는 경우에는 pattern 수정
        val pattern = "https://github\\.com/"
        val regex = Pattern.compile(pattern)
        val matcher = regex.matcher(url)
        return matcher.find()
    }


    private val _isStep1DataValid = MutableLiveData<Boolean>()
    val isStep1DataValid: LiveData<Boolean> = _isStep1DataValid

    fun setIsStep1DataValid() {
        _isStep1DataValid.value = isTitleValid() && isMissionRepoUrlValid()
    }

    /** step2 */
    val techTagList = MutableLiveData<MutableList<String>>(mutableListOf())

    private val _isStep2DataValid = MutableLiveData<Boolean>()
    val isStep2DataValid: LiveData<Boolean> = _isStep2DataValid

    fun setIsTechTagValid() {
        _isStep2DataValid.value = (techTagList.value?.size ?: 0) > 0
    }

    /** step3 */
    val descriptionEditTextData = MutableLiveData(
        EditTextData(
            text = MutableLiveData(""),
            infoStatus = MutableLiveData(InfoType.NONE),
            maxLength = 1000,
            hint = "내용을 입력하세요.",
            wordCountVisibility = false
        )
    )

    val description: LiveData<String> = descriptionEditTextData.value?.text ?: MutableLiveData("")

    fun updateMissionDescriptionInfoStatus() {
        descriptionEditTextData.value?.infoStatus?.value =
            if (isConsistOnlyWithSpace(description)) InfoType.SPACE_ONLY_NOT_ALLOWED
            else InfoType.NONE
    }

    private fun isMissionDescriptionValid(): Boolean {
        return descriptionEditTextData.value?.infoStatus?.value == InfoType.NONE
                && descriptionEditTextData.value?.text?.value?.isNotBlank() == true
    }

    val recommendGroupEditTextData = MutableLiveData(
        EditTextData(
            text = MutableLiveData(""),
            infoStatus = MutableLiveData(InfoType.NONE),
            maxLength = 1000,
            hint = "내용을 입력하세요.",
            wordCountVisibility = false
        )
    )

    private val recommendGroup: LiveData<String> =
        recommendGroupEditTextData.value?.text ?: MutableLiveData("")

    val notRecommendGroupEditTextData = MutableLiveData(
        EditTextData(
            text = MutableLiveData(""),
            infoStatus = MutableLiveData(InfoType.NONE),
            maxLength = 1000,
            hint = "내용을 입력하세요.",
            wordCountVisibility = false
        )
    )

    private val notRecommendGroup: LiveData<String> =
        notRecommendGroupEditTextData.value?.text ?: MutableLiveData("")

    private val _isStep3DataValid = MutableLiveData<Boolean>()
    val isStep3DataValid: LiveData<Boolean> = _isStep3DataValid

    fun setIsStep3DataValid() {
        _isStep3DataValid.value = isMissionDescriptionValid()
    }

    /** step4 */
    private val _numOfRecruits = MutableLiveData<Int>()
    private val numOfRecruits: LiveData<Int> = _numOfRecruits

    fun setNumOfRecruits(num: String) {
        _numOfRecruits.value = if (num.isEmpty()) null else num.toInt()
    }

    private val _price = MutableLiveData<Int>()
    private val price: LiveData<Int> = _price

    fun setPrice(price: String) {
        _price.value = if (price.isEmpty()) null else price.toInt()
    }

    private val _isStep4DataValid = MutableLiveData<Boolean>()
    val isStep4DataValid: LiveData<Boolean> = _isStep4DataValid

    fun setIsStep4DataValid() {
        _isStep4DataValid.value = (numOfRecruits.value ?: 0) > 0 && (price.value != null)
    }

    /** step5 */
    val onDateClicked = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        val date = LocalDate.of(year, month + 1, day)
        _registrationDueDate.value = date
    }

    private val _registrationDueDate = MutableLiveData<LocalDate>()
    val registrationDueDate: LiveData<LocalDate> = _registrationDueDate

    private val _isStep5DataValid = MutableLiveData<Boolean>()
    val isStep5DataValid: LiveData<Boolean> = _isStep5DataValid

    fun setIsStep5DataValid() {
        _isStep5DataValid.value = registrationDueDate.value != null
    }

    private val _createMissionState: MutableLiveData<NetworkState<PostMissionResponseVO>> =
        MutableLiveData(NetworkState.Init)
    val createMissionState: LiveData<NetworkState<PostMissionResponseVO>> = _createMissionState


    private fun createPostMissionRequestDTO(): PostMissionRequestDTO {
        val formattedDate = registrationDueDate.value?.format(isoStyleFormatter)
        val date = formattedDate?.substring(0, 10)
        return PostMissionRequestDTO(
            description = requireNotNull(description.value),
            maxPeopleNumber = requireNotNull(numOfRecruits.value),
            missionRepositoryUrl = repositoryUrl.value,
            notRecommendTo = notRecommendGroup.value,
            recommendTo = recommendGroup.value,
            price = requireNotNull(price.value),
            registrationDueDate = requireNotNull(date),
            tagList = requireNotNull(techTagList.value),
            title = requireNotNull(title.value)
        )
    }

    fun createMission() {
        val mission = createPostMissionRequestDTO()
        viewModelScope.launch(lgtmErrorHandler) {
            missionRepository.createMission(mission)
                .onSuccess {
                    _createMissionState.postValue(NetworkState.Success(it))
                }.onFailure {
                    val errorMessage = if (it is LgtmResponseException) it.message else "미션 생성 실패"
                    _createMissionState.postValue(NetworkState.Failure(errorMessage))
                    throw it
                }
        }
    }
}