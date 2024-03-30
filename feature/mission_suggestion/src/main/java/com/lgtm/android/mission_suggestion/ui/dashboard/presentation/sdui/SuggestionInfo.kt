package com.lgtm.android.mission_suggestion.ui.dashboard.presentation.sdui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lgtm.android.common_ui.theme.LGTMTheme
import com.lgtm.domain.mission_suggestion.SuggestionHeaderVO

@Composable
fun SuggestionInfo(
    suggestionHeader: SuggestionHeaderVO
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = LGTMTheme.colors.gray_7,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {
        Text(
            text = suggestionHeader.title,
            style = LGTMTheme.typography.body2,
            color = LGTMTheme.colors.white
        )
        Text(
            modifier = Modifier.padding(
                top = 8.dp,
                start = 4.dp,
                end = 4.dp
            ),
            text = suggestionHeader.description,
            style = LGTMTheme.typography.body2,
            color = LGTMTheme.colors.white
        )
    }
}