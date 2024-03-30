package com.lgtm.android.mission_suggestion.ui.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lgtm.android.common_ui.R
import com.lgtm.android.common_ui.components.buttons.BackButton
import com.lgtm.android.common_ui.model.SuggestionUI
import com.lgtm.android.common_ui.theme.LGTMTheme
import com.lgtm.android.common_ui.util.UiState
import com.lgtm.android.common_ui.util.throttleClickable
import com.lgtm.android.mission_suggestion.ui.dashboard.SuggestionDashboardViewModel
import com.lgtm.android.mission_suggestion.ui.dashboard.presentation.sdui.SuggestionContent
import com.lgtm.android.mission_suggestion.ui.dashboard.presentation.sdui.SuggestionInfo
import com.lgtm.android.mission_suggestion.ui.dashboard.presentation.sdui.SuggestionListEmpty
import com.lgtm.domain.mission_suggestion.SuggestionContent
import com.lgtm.domain.mission_suggestion.SuggestionHeaderVO
import com.lgtm.domain.mission_suggestion.SuggestionViewType

const val LAZYCOLUMN_PADDING = 20

@Composable
fun SuggestionDashboardScreen(
    viewModel: SuggestionDashboardViewModel = hiltViewModel()
) {
    val suggestionDashboardState: UiState<List<SuggestionContent>> by viewModel.suggestionDashboardState.collectAsStateWithLifecycle()

    ConstraintLayout(
        modifier = Modifier
            .background(color = LGTMTheme.colors.gray_3)
            .fillMaxSize()
    ) {
        val (suggestionSection, createBtn) = createRefs()

        Column(
            modifier = Modifier.constrainAs(suggestionSection){
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
        ) {
            SuggestionHeader{ viewModel.goBack() }

            when(suggestionDashboardState) {
                is UiState.Init -> { /* no-op */ }
                is UiState.Success -> {
                    SuggestionList(
                        modifier = Modifier
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints.offset(vertical = LAZYCOLUMN_PADDING*2.dp.roundToPx()))
                                layout(
                                    placeable.width,
                                    placeable.height
                                ){ placeable.place(0, 0) }
                            },
                        suggestionList = (suggestionDashboardState as UiState.Success).data,
                        onSuggestionClick = viewModel::moveToSuggestionDetail
                    )
                }
                is UiState.Failure -> { /* no-op */ }
            }
        }

        CreateSuggestionButton(
            modifier = Modifier.constrainAs(createBtn) {
                bottom.linkTo(parent.bottom, margin = 25.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            visibility = viewModel.createBtnVisibility
        ) { viewModel.moveToCreateSuggestion() }
    }
}

/* 헤더 부분 */
@Composable
fun SuggestionHeader(
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = LGTMTheme.colors.white,
                shape = RoundedCornerShape(
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            )
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            )
            .zIndex(1f)
    ) {
        val (backBtn, title) = createRefs()

        BackButton(
            modifier = Modifier.constrainAs(backBtn) {
                top.linkTo(parent.top, margin = 30.dp)
                start.linkTo(parent.start, margin = 20.dp)
                bottom.linkTo(parent.bottom, margin = 20.dp)
            }
        ) { onBackButtonClick() }

        Text(
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
            text = stringResource(id = R.string.mission_recommendation),
            color = LGTMTheme.colors.black,
            style = LGTMTheme.typography.body1B
        )
    }
}

/* 미션 제안 리스트 부분 */

@Composable
fun SuggestionList(
    modifier: Modifier = Modifier,
    suggestionList: List<SuggestionContent>,
    onSuggestionClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(suggestionList.size) { index ->
            // 첫 번째 요소 위에 Spacer 배치
            if (index == 0) {
                Spacer(modifier = Modifier.height(25.dp))
            }
            getSuggestionViewByType(suggestionList[index], onSuggestionClick)
            // 마지막 요소 아래에 Spacer 배치
            if (index == suggestionList.size - 1) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun getSuggestionViewByType(
    suggestionContent: SuggestionContent,
    onSuggestionClick: (Int) -> Unit
) {
    when(suggestionContent.viewType) {
        SuggestionViewType.HEADER -> SuggestionInfo(suggestionContent as SuggestionHeaderVO)
        SuggestionViewType.CONTENT -> SuggestionContent(suggestionContent as SuggestionUI, onSuggestionClick)
        SuggestionViewType.EMPTY -> SuggestionListEmpty()
    }
}

/* 미션 제안 생성 */

@Composable
fun CreateSuggestionButton(
    modifier: Modifier = Modifier,
    visibility: Boolean,
    onCreateSuggestionClick: () -> Unit
) {
    if (visibility) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(
                    color = LGTMTheme.colors.green,
                    shape = RoundedCornerShape(5.dp)
                )
                .zIndex(10f)
                .throttleClickable(true) {
                    onCreateSuggestionClick()
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier
                    .padding(vertical = 14.dp),
                text = stringResource(id = R.string.recommend_mission),
                style = LGTMTheme.typography.body1M,
                color = LGTMTheme.colors.black
            )
        }
    }
}