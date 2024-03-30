package com.lgtm.android.mission_suggestion.ui.dashboard

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lgtm.android.common_ui.base.BaseComposeActivity
import com.lgtm.android.common_ui.theme.LGTMTheme
import com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract.SuggestionDashboardUiEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SuggestionDashboardActivity : BaseComposeActivity() {
    private val suggestionDashboardViewModel by viewModels<SuggestionDashboardViewModel>()

    override fun initializeViewModel() {
        viewModel = suggestionDashboardViewModel
    }

    @Composable
    override fun Content() {
        LGTMTheme {
            // TODO: SuggestionDashboardScreen 배치
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeUiEffect()
        fetchSuggestionList()
    }

    private fun fetchSuggestionList() {
        suggestionDashboardViewModel.fetchSuggestionList()
    }

    private fun observeUiEffect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                suggestionDashboardViewModel.suggestionDashboardUiEffect.collect { effect ->
                    when (effect) {
                        is SuggestionDashboardUiEffect.GoBack -> {
                            finish()
                        }

                        is SuggestionDashboardUiEffect.CreateSuggestion -> {
                            lgtmNavigator.navigateToCreateSuggestion(this@SuggestionDashboardActivity)
                        }

                        is SuggestionDashboardUiEffect.SuggestionDetail -> {
                            lgtmNavigator.navigateToSuggestionDetail(
                                this@SuggestionDashboardActivity,
                                effect.suggestionId
                            )
                        }
                    }
                }
            }
        }
    }
}