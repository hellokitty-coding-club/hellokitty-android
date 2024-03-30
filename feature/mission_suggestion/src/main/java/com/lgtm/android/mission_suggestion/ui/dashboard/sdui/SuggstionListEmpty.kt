package com.lgtm.android.mission_suggestion.ui.dashboard.sdui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lgtm.android.common_ui.R
import com.lgtm.android.common_ui.theme.LGTMTheme

@Composable
fun SuggestionListEmpty() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = LGTMTheme.colors.gray_3,
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.padding(top = 43.dp),
            painter = painterResource(id = R.drawable.img_empty_gray_1),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(top = 5.dp),
            text = stringResource(id = R.string.no_recommendation_yet),
            style = LGTMTheme.typography.body2,
            color = LGTMTheme.colors.gray_6
        )
    }
}