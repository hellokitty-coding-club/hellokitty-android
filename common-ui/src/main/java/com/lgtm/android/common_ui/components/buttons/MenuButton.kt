package com.lgtm.android.common_ui.components.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lgtm.android.common_ui.R
import com.lgtm.android.common_ui.theme.LGTMTheme

@Composable
fun MenuButton(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .border(
                width = 1.dp,
                color = LGTMTheme.colors.gray_3,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = LGTMTheme.colors.white,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(3.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_menu_black),
            contentDescription = null
        )
    }
}