package com.lgtm.android.mission_suggestion.ui.create_suggestion.presentation

import android.view.Gravity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lgtm.android.common_ui.R
import com.lgtm.android.common_ui.components.buttons.BackButton
import com.lgtm.android.common_ui.components.buttons.ConfirmButtonBackgroundColor
import com.lgtm.android.common_ui.components.dialog.LgtmConfirmationDialog
import com.lgtm.android.common_ui.model.EditTextData
import com.lgtm.android.common_ui.theme.LGTMTheme
import com.lgtm.android.common_ui.ui.LGTMEditText
import com.lgtm.android.common_ui.util.UiState
import com.lgtm.android.common_ui.util.throttleClickable
import com.lgtm.android.mission_suggestion.ui.create_suggestion.CreateSuggestionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateSuggestionScreen(
    viewModel: CreateSuggestionViewModel = hiltViewModel(),
    onBackButtonClick: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    val createSuggestionState by viewModel.createSuggestionState.collectAsStateWithLifecycle()
    val isSuggestionEmpty by viewModel.isSuggestionEmpty.collectAsStateWithLifecycle()
    val suggestionTitleTextData by viewModel.suggestionTitleTextData.collectAsStateWithLifecycle()
    val suggestionContentTextData by viewModel.suggestionContentTextData.collectAsStateWithLifecycle()
    val isSuggestionValid by viewModel.isSuggestionValid.collectAsStateWithLifecycle()

    LgtmConfirmationDialog(
        bottomSheetState = bottomSheetState,
        dialogTitle = stringResource(id = R.string.want_to_stop_creating_suggestion),
        dialogDescription = stringResource(id = R.string.content_will_not_be_saved),
        onClickConfirm = onBackButtonClick,
        confirmBtnBackground = ConfirmButtonBackgroundColor.GRAY
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxHeight()
                .background(color = LGTMTheme.colors.white)
                .imePadding()
        ) {

            when(createSuggestionState) {
                is UiState.Init -> {
                    val (backButton, suggestionSection, nextButton) = createRefs()

                    SuggestionSection(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .constrainAs(suggestionSection) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        suggestionTitleEditTextData = suggestionTitleTextData,
                        suggestionContentEditTextData = suggestionContentTextData,
                        updateTitleEditTextData = viewModel::updateSuggestionTitleTextData,
                        updateContentEditTextData = viewModel::updateSuggestionContentTextData
                    )

                    CreateSuggestionNextButton(
                        modifier = Modifier
                            .constrainAs(nextButton) {
                                bottom.linkTo(parent.bottom, margin = 28.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        enabledState = isSuggestionValid,
                        onClick = viewModel::createSuggestion
                    )

                    CreateSuggestionBackButton(
                        modifier = Modifier
                            .constrainAs(backButton) {
                                top.linkTo(parent.top, margin = 30.dp)
                                start.linkTo(parent.start, margin = 20.dp)
                            },
                        isSuggestionEmpty = isSuggestionEmpty,
                        onBackButtonClick = onBackButtonClick,
                        showStopCreationDialog = {
                            coroutineScope.launch {
                                bottomSheetState.show()
                            }
                        }
                    )

                    BackHandler {
                        if (!isSuggestionEmpty) {
                            coroutineScope.launch {
                                bottomSheetState.show()
                            }
                        } else {
                            onBackButtonClick()
                        }
                    }
                }
                is UiState.Success -> { onBackButtonClick() }
                is UiState.Failure -> { /* no-op */ }
            }
        }
    }
}

@Composable
fun CreateSuggestionBackButton(
    modifier: Modifier = Modifier,
    isSuggestionEmpty: Boolean,
    onBackButtonClick: () -> Unit,
    showStopCreationDialog: () -> Unit
) {
    BackButton(modifier) {
        if (!isSuggestionEmpty) {
            showStopCreationDialog()
        } else {
            onBackButtonClick()
        }
    }
}

@Composable
fun CreateSuggestionNextButton(
    modifier: Modifier = Modifier,
    enabledState: Boolean,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(
                color = if (enabledState) LGTMTheme.colors.green else LGTMTheme.colors.gray_2,
                shape = RoundedCornerShape(5.dp)
            )
            .throttleClickable(enabledState) {
                onClick()
            },
        contentAlignment = Alignment.Center,

        ) {
        Text(
            modifier = Modifier
                .padding(vertical = 14.dp),
            text = stringResource(id = R.string.suggestion_next),
            style = LGTMTheme.typography.body1M,
            color = if (enabledState) LGTMTheme.colors.black else LGTMTheme.colors.gray_4
        )
    }
}

@Composable
fun SuggestionSection(
    modifier: Modifier = Modifier,
    suggestionTitleEditTextData: EditTextData,
    suggestionContentEditTextData: EditTextData,
    updateTitleEditTextData: () -> Unit,
    updateContentEditTextData: () -> Unit
) {
    Column(
        modifier
            .padding(
                start = 20.dp,
                end = 20.dp,
                bottom = 193.dp
            ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        SuggestionTitle(
            suggestionTitleEditTextData,
            updateTitleEditTextData,
        )
        SuggestionContent(
            suggestionContentEditTextData,
            updateContentEditTextData
        )
    }
}

private const val TIME_FOR_KEYBOARD_DELAY = 420L

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SuggestionTitle(
    suggestionTitleEditTextData: EditTextData,
    updateTitleEditTextData: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val title = remember { suggestionTitleEditTextData }

    val titleSectionBringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .bringIntoViewRequester(titleSectionBringIntoViewRequester)
            .padding(top = 107.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start,
    ) {
        SubTitleWithDescription(
            subTitle = R.string.mission_suggestion_title,
            description = R.string.mission_suggestion_title_description
        )

        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                LGTMEditText(context).apply {
                    setLifecycleOwner(lifecycleOwner)
                    bindStateEditTextData(title)
                    setMaxLine(1)
                    onTextChangedListener {
                        updateTitleEditTextData()
                    }
                    onFocusChangedListener { isFocused ->
                        if (isFocused) {
                            coroutineScope.launch {
                                delay(TIME_FOR_KEYBOARD_DELAY)
                                titleSectionBringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SuggestionContent(
    suggestionContentEditTextData: EditTextData,
    updateContentEditTextData: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val content = remember { suggestionContentEditTextData }

    val contentSectionBringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .bringIntoViewRequester(contentSectionBringIntoViewRequester)
            .padding(top = 42.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start,
    ) {
        SubTitleWithDescription(
            subTitle = R.string.mission_suggestion_content,
            description = R.string.mission_suggestion_content_description
        )

        AndroidView(
            modifier = Modifier
                .fillMaxWidth(),
            factory = { context ->
                LGTMEditText(context).apply {
                    setLifecycleOwner(lifecycleOwner)
                    bindStateEditTextData(content)
                    setMaxLine(5)
                    setMinLine(5)
                    setGravity(Gravity.TOP)
                    onTextChangedListener {
                        updateContentEditTextData()
                    }
                    onFocusChangedListener { isFocused ->
                        if (isFocused) {
                            coroutineScope.launch {
                                delay(TIME_FOR_KEYBOARD_DELAY)
                                contentSectionBringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun SubTitleWithDescription(
    subTitle: Int,
    description: Int
) {
    Text(
        modifier = Modifier.padding(bottom = 10.dp),
        text = stringResource(id = subTitle),
        style = LGTMTheme.typography.heading4B,
        color = LGTMTheme.colors.black
    )

    Text(
        modifier = Modifier.padding(bottom = 14.dp),
        text = stringResource(id = description),
        style = LGTMTheme.typography.body2,
        color = LGTMTheme.colors.gray_6
    )
}