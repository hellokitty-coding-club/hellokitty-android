package com.lgtm.android.create_mission

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.lgtm.android.common_ui.adapter.ViewPagerAdapter
import com.lgtm.android.common_ui.base.BaseActivity
import com.lgtm.android.create_mission.databinding.ActivityCreateMissionBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.Type

@AndroidEntryPoint
class CreateMissionActivity :
    BaseActivity<ActivityCreateMissionBinding>(R.layout.activity_create_mission) {
    private val createMissionViewModel by viewModels<CreateMissionViewModel>()
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun initializeViewModel() {
        viewModel = createMissionViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdapter()
        attachViewPagerToIndicator()
        disableViewPagerSwipe()
        setOnBackButtonClickListener()
        disableClickDotIndicator()
    }

    private fun initAdapter() {
        binding.vpMission.adapter = ViewPagerAdapter(this).also { viewPagerAdapter = it }
        viewPagerAdapter.fragmentList =
            listOf(
                CreateMissionStep1Fragment(),
                CreateMissionStep2Fragment(),
                CreateMissionStep3Fragment(),
                CreateMissionStep4Fragment(),
                CreateMissionStep5Fragment()
            )
    }

    private fun attachViewPagerToIndicator() {
        binding.wormDotsIndicator.attachTo(binding.vpMission)
    }

    private fun disableViewPagerSwipe() {
        binding.vpMission.isUserInputEnabled = false
    }

    private fun setOnBackButtonClickListener() {
        binding.ivBack.setOnClickListener {
            val currentItem = binding.vpMission.currentItem
            if (currentItem == 0) finish()
            else binding.vpMission.setCurrentItem(currentItem - 1, true)
        }
    }

    private fun disableClickDotIndicator() {
        if (!BuildConfig.DEBUG)
            binding.wormDotsIndicator.dotsClickable = false
    }

    fun onNextButtonClick(currentFragment: Type) {
        when (currentFragment) {
            CreateMissionStep1Fragment::class.java -> binding.vpMission.setCurrentItem(1, true)
            CreateMissionStep2Fragment::class.java -> binding.vpMission.setCurrentItem(2, true)
            CreateMissionStep3Fragment::class.java -> binding.vpMission.setCurrentItem(3, true)
            CreateMissionStep4Fragment::class.java -> binding.vpMission.setCurrentItem(4, true)
            CreateMissionStep5Fragment::class.java -> Toast.makeText(
                this,
                "서버 연동 구현해야함!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}