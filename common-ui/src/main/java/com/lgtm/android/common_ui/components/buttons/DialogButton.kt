package com.lgtm.android.common_ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.lgtm.android.common_ui.R
import com.lgtm.android.common_ui.theme.body1M
import com.lgtm.android.common_ui.util.throttleClickable

@Composable
fun DialogButton(
    modifier: Modifier = Modifier,
    text: String,
    confirmBtnBackground: ConfirmButtonBackgroundColor = ConfirmButtonBackgroundColor.GRAY,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = colorResource(id = confirmBtnBackground.background),
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = if (confirmBtnBackground == ConfirmButtonBackgroundColor.GREEN) 0.dp else 1.dp,
                color = colorResource(id = R.color.gray_3),
                shape = RoundedCornerShape(10.dp)
            )
            .throttleClickable(
                enabled = true,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(
                vertical = 11.dp
            ),
            text = text,
            style = Typography.body1M,
            color = colorResource(id = R.color.black)
        )
    }
}

enum class ConfirmButtonBackgroundColor(val background: Int) {
    GREEN(R.color.green),
    GRAY(R.color.gray_1)
}