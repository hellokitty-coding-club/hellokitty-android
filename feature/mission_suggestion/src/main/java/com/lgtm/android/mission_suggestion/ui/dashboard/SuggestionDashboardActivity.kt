package com.lgtm.android.mission_suggestion.ui.dashboard

import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lgtm.android.common_ui.base.BaseComposeActivity
import com.lgtm.android.common_ui.theme.LGTMTheme
import com.lgtm.android.mission_suggestion.ui.dashboard.presentation.SuggestionDashboardScreen
import com.lgtm.android.mission_suggestion.ui.dashboard.presentation.contract.SuggestionDashboardUiEffect
import com.lgtm.domain.logging.SwmCommonLoggingScheme
import com.lgtm.domain.mission_suggestion.SuggestionContent
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
            SuggestionDashboardScreen()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeUiEffect()
    }

    override fun onResume() {
        super.onResume()
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

                        is SuggestionDashboardUiEffect.ShotSuggestionClickLogging -> {
                            val scheme = getHomeSuggestionItemClickLoggingScheme(effect.suggestionContent)
                            suggestionDashboardViewModel.shotSwmLogging(scheme)
                        }
                    }
                }
            }
        }
    }

    private fun getHomeSuggestionItemClickLoggingScheme(suggestionContent: SuggestionContent): SwmCommonLoggingScheme {
        return SwmCommonLoggingScheme.Builder()
            .setEventLogName("suggestionItemClick")
            .setScreenName(this.javaClass)
            .setLogData(mapOf("suggestionItem" to suggestionContent))
            .build()
    }
}