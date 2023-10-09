package com.lgtm.android.common_ui.model.mapper

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.lgtm.android.common_ui.R
import com.lgtm.android.common_ui.constant.MissionDetailButtonStatus.Companion.getButtonStatusUI
import com.lgtm.android.common_ui.constant.MissionStatusUI.Companion.getMissionStatusUI
import com.lgtm.android.common_ui.constant.ProcessStateUI.Companion.getProcessStateUI
import com.lgtm.android.common_ui.model.DashboardUI
import com.lgtm.android.common_ui.model.MemberMissionStatusUI
import com.lgtm.android.common_ui.model.MissionDetailUI
import com.lgtm.android.common_ui.model.MissionHistoryUI
import com.lgtm.android.common_ui.model.PingPongJuniorUI
import com.lgtm.android.common_ui.model.ProfileGlanceUI
import com.lgtm.domain.constants.ProcessState
import com.lgtm.domain.constants.Role
import com.lgtm.domain.constants.UNKNOWN
import com.lgtm.domain.entity.response.DashboardVO
import com.lgtm.domain.entity.response.MemberMissionStatusVO
import com.lgtm.domain.entity.response.MissionDetailVO
import com.lgtm.domain.entity.response.MissionHistoryVO
import com.lgtm.domain.entity.response.PingPongJuniorVO
import com.lgtm.domain.entity.response.ProfileVO
import com.lgtm.domain.profile.profileViewType.ProfileGlance

fun MissionDetailVO.toUiModel(): MissionDetailUI = MissionDetailUI(
    currentPeopleNumber = currentPeopleNumber,
    description = description,
    maxPeopleNumber = maxPeopleNumber,
    memberProfile = memberProfile.toUiModel(memberType),
    memberType = memberType,
    missionId = missionId,
    missionRepositoryUrl = missionRepositoryUrl,
    missionStatus = getMissionStatusUI(missionStatus),
    missionTitle = missionTitle,
    notRecommendTo = notRecommendTo,
    price = price,
    recommendTo = recommendTo,
    remainingRegisterDays = remainingRegisterDays,
    scraped = scraped,
    techTagList = techTagList,
    missionDetailButtonStatusUI = getButtonStatusUI(missionDetailStatus)
)

fun ProfileVO.toUiModel(role: Role): ProfileGlanceUI = ProfileGlanceUI(
    memberId = memberId,
    profileImage = profileImageUrl,
    nickname = nickname,
    githubId = githubId,
    detailInfoLabel = when (role) {
        Role.REVIEWER -> R.string.company_slash_position
        Role.REVIEWEE -> R.string.education
    },
    detailInfo = when (role) {
        Role.REVIEWER -> "$company / $position"
        Role.REVIEWEE -> educationalHistory
    } ?: UNKNOWN
)

fun ProfileGlance.toUiModel(): ProfileGlanceUI = ProfileGlanceUI(
    memberId = memberId,
    profileImage = profileImage,
    nickname = nickname,
    githubId = githubId,
    detailInfoLabel = when (memberType) {
        Role.REVIEWER -> R.string.company_slash_position
        Role.REVIEWEE -> R.string.education
    },
    detailInfo = when (memberType) {
        Role.REVIEWER -> "$company / $position"
        Role.REVIEWEE -> educationalHistory
    } ?: UNKNOWN
)

fun DashboardVO.toUiModel() = DashboardUI(
    memberInfoList = memberInfoList.map { it.toUiModel() },
    missionName = missionName,
    techTagList = techTagList
)

fun MemberMissionStatusVO.toUiModel() = MemberMissionStatusUI(
    githubId = githubId,
    githubPrUrl = githubPrUrl,
    memberId = memberId,
    nickname = nickname,
    missionFinishedDate = missionFinishedDate,
    paymentDate = paymentDate,
    processStatus = getProcessStateUI(processStatus),
    profileImageUrl = profileImageUrl,
    isMissionSubmitted = isMissionSubmitted,
)

fun PingPongJuniorVO.toUiModel(role: Role) = PingPongJuniorUI(
    missionName = missionName,
    techTagList = techTagList,
    processStatus = processStatus,
    accountInfo = accountInfo,
    missionHistory = missionHistory.toUiModel(role, processStatus),
    reviewId = reviewId,
    pullRequestUrl = pullRequestUrl,
    buttonTitle = buttonTitle
)
fun MissionHistoryVO.toUiModel(
    role: Role,
    processStatus: ProcessState
): MissionHistoryUI {
    fun createSpannableText(text: String, redTextStart: Int, redTextEnd: Int): SpannableString {
        val spannableText = SpannableString(text)
        spannableText.setSpan(
            ForegroundColorSpan(Color.RED),
            redTextStart,
            redTextEnd,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return spannableText
    }

    val juniorWaitingForPaymentDetail = createSpannableText(
        "리뷰어에게 참여비를 입금한 후, 완료 버튼을 누르세요.",
        18,
        29
    )

    val juniorPaymentConfirmationDateDetail = createSpannableText(
        "리뷰어가 입금 내역을 확인하고 있어요.",
        0,
        20
    )

    val seniorPaymentConfirmationDateDetail = createSpannableText(
        "입금자명: 김하나",
        0,
        0
    )

    val juniorMissionProceedingDetail = createSpannableText(
        "미션 제출 후, 리뷰 요청 버튼을 누르세요.",
        0,
        23
    )

    val juniorCodeReviewDetail = createSpannableText(
        "리뷰어가 제출된 미션을 확인중이에요.",
        5,
        15
    )

    val seniorCodeReviewDetail = createSpannableText(
        "깃허브에서 리뷰 작성후, 완료 버튼을 누르세요.",
        6,
        25
    )

    val waitingForPaymentDetail =
        if (role == Role.REVIEWEE && processStatus == ProcessState.WAITING_FOR_PAYMENT) juniorWaitingForPaymentDetail
        else null

    val paymentConfirmationDetail =
        if (role == Role.REVIEWEE && processStatus == ProcessState.PAYMENT_CONFIRMATION) juniorPaymentConfirmationDateDetail
        else if (role == Role.REVIEWER && processStatus == ProcessState.PAYMENT_CONFIRMATION) seniorPaymentConfirmationDateDetail
        else null

    val missionProceedingDetail =
        if (role == Role.REVIEWEE && processStatus == ProcessState.MISSION_PROCEEDING) juniorMissionProceedingDetail
        else null

    val codeReviewDetail =
        if (role == Role.REVIEWEE && processStatus == ProcessState.CODE_REVIEW) juniorCodeReviewDetail
        else if (role == Role.REVIEWER && processStatus == ProcessState.CODE_REVIEW) seniorCodeReviewDetail
        else null

    return MissionHistoryUI(
        waitingForPaymentDate = waitingForPaymentDate,
        paymentConfirmationDate = paymentConfirmationDate,
        missionProceedingDate = missionProceedingDate,
        codeReviewDate = codeReviewDate,
        feedbackReviewedDate = feedbackReviewedDate,
        missionFinishedDate = missionFinishedDate,
        waitingForPaymentDetail = waitingForPaymentDetail,
        paymentConfirmationDetail = paymentConfirmationDetail,
        missionProceedingDetail = missionProceedingDetail,
        codeReviewDetail = codeReviewDetail,
    )
}
