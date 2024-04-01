package com.lgtm.android.common_ui.viewholder

import com.lgtm.android.common_ui.databinding.ItemSduiSubItemBinding
import com.lgtm.android.common_ui.util.setOnThrottleClickListener
import com.lgtm.domain.server_drive_ui.SduiContent
import com.lgtm.domain.server_drive_ui.SduiTheme
import com.lgtm.domain.server_drive_ui.SectionSubItemVO

class SduiSubItemViewHolder(
    private val binding: ItemSduiSubItemBinding
) : SduiBaseHolder(binding) {
    private lateinit var navigateToRecommendationDashboard: (SduiContent) -> Unit

    override fun bind(theme: SduiTheme, viewContent: SduiContent) {
        binding.data = viewContent as SectionSubItemVO
        binding.theme = theme
        binding.btnMissionRecommend.setOnThrottleClickListener {
            // todo 추후 appUrl을 활용한 화면 전환 처리
            navigateToRecommendationDashboard(viewContent)
        }
    }

    fun setNavigateToRecommendationDashboard(navigateToRecommendationDashboard: (SduiContent) -> Unit) {
        this.navigateToRecommendationDashboard = navigateToRecommendationDashboard
    }
}