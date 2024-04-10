package com.lgtm.android.main.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.lgtm.android.common_ui.adapter.SduiAdapter
import com.lgtm.android.common_ui.base.BaseFragment
import com.lgtm.android.common_ui.util.setOnThrottleClickListener
import com.lgtm.android.main.R
import com.lgtm.android.main.databinding.FragmentHomeBinding
import com.lgtm.domain.constants.Role
import com.lgtm.domain.logging.FirstMissionClickScheme
import com.lgtm.domain.logging.HomeMissionClickScheme
import com.lgtm.domain.logging.SwmCommonLoggingScheme
import com.lgtm.domain.server_drive_ui.SduiContent
import com.lgtm.domain.server_drive_ui.SectionItemVO
import com.lgtm.domain.server_drive_ui.SectionSubItemVO
import com.swm.logging.android.logging_scheme.SWMLoggingScheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    private val homeViewModel by viewModels<HomeViewModel>()
    private lateinit var commonAdapter: SduiAdapter
    private var isFirstMissionClick = false
    private val entryTime = System.currentTimeMillis()
    private var firstMissionClickTime by Delegates.notNull<Long>()

    override fun initializeViewModel() {
        viewModel = homeViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViewModel()
        initAdapter()
        submitDataWhenDataChanged()
        setUpNotificationClickListener()
        onClickNewMissionButton()
        onClickServiceGuideButton()
        observeNewNotification()
    }

    override fun onResume() {
        super.onResume()
        getHomeInfo()
        checkNewNotification()
    }

    private fun getHomeInfo() {
        homeViewModel.getHomeInfo()
    }

    private fun checkNewNotification() {
        homeViewModel.hasNewNotification()
    }

    private fun observeNewNotification() {
        homeViewModel.hasNewNotification.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.ivNewNotification.visibility = View.VISIBLE
            } else {
                binding.ivNewNotification.visibility = View.GONE
            }
        }
    }

    private fun setupViewModel() {
        binding.viewModel = homeViewModel
    }

    private fun setUpNotificationClickListener() {
        binding.ivNotification.setOnThrottleClickListener {
            moveToNotificationCenter()
        }
    }

    private fun initAdapter() {
        commonAdapter = SduiAdapter(::onClickMissionItem, ::onClickRecommendation)
        binding.rvSdui.adapter = commonAdapter
    }

    private fun onClickMissionItem(sduiContent: SduiContent) {
        if (!isFirstMissionClick) logFirstMissionClick(sduiContent)
        logMissionClick(sduiContent)
        moveToMissionDetail((sduiContent as SectionItemVO).missionId)
    }

    private fun onClickRecommendation(sduiContent: SduiContent) {
        moveToRecommendationDashboard()
        logMissionSuggestionClick(sduiContent)
    }

    private fun logFirstMissionClick(sduiContent: SduiContent) {
        firstMissionClickTime = System.currentTimeMillis()
        isFirstMissionClick = true
        val firstMissionClickScheme = FirstMissionClickScheme.Builder()
            .setMissionId((sduiContent as SectionItemVO).missionId)
            .setSpendingTime(firstMissionClickTime - entryTime)
            .build()
        homeViewModel.shotFirstMissionClickLogging(firstMissionClickScheme)
    }

    private fun logMissionClick(sduiContent: SduiContent) {
        val scheme = getHomeMissionClickLoggingScheme(sduiContent)
        homeViewModel.shotHomeMissionClickLogging(scheme)
    }

    private fun getHomeMissionClickLoggingScheme(sduiContent: SduiContent): SWMLoggingScheme {
        return HomeMissionClickScheme.Builder()
            .setMissionContent(sduiContent)
            .build()
    }

    private fun moveToMissionDetail(missionId: Int) {
        lgtmNavigator.navigateToMissionDetail(requireContext(), missionId)
    }

    private fun moveToRecommendationDashboard() {
        lgtmNavigator.navigateToSuggestionDashboard(requireContext())
    }

    private fun moveToNotificationCenter() {
        lgtmNavigator.navigateToNotificationCenter(requireContext())
    }

    private fun submitDataWhenDataChanged() {
        homeViewModel.sduiList.observe(viewLifecycleOwner) {
            commonAdapter.submitList(it)
        }
    }

    private fun onClickNewMissionButton() {
        binding.fabCreateMission.setOnThrottleClickListener {
            lgtmNavigator.navigateToCreateMission(requireContext())
        }
    }

    private fun onClickServiceGuideButton() {
        val role = homeViewModel.getUserRole()
        binding.ivServiceGuide.setOnThrottleClickListener {
            val url = when (role) {
                Role.REVIEWER -> "https://www.notion.so/team-hkcc/c1933575315e4b50aedbdd6b39069d3e?pvs=4"
                Role.REVIEWEE -> "https://www.notion.so/team-hkcc/5abf8b03763a4f0d90cb2d463f6d46b4?pvs=4"
            }
            lgtmNavigator.openUrlInBrowser(requireContext(), url)
        }
    }

    private fun logMissionSuggestionClick(sduiContent: SduiContent) {
        val role = homeViewModel.getUserRole()
        val missionSuggestionClickScheme = SwmCommonLoggingScheme.Builder()
            .setEventLogName("suggestionClick")
            .setScreenName(this.javaClass)
            .setLogData(mapOf(
                "memberType" to role.role,
                "suggestionTitle" to homeViewModel.getMissionSuggestionTitle(),
                "suggestionDescription" to (sduiContent as SectionSubItemVO).text
            ))
            .build()
        homeViewModel.shotMissionSuggestionClickLogging(missionSuggestionClickScheme)
    }

}