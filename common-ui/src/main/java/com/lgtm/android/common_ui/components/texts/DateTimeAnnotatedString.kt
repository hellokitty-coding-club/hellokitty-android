package com.lgtm.android.common_ui.components.texts

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.lgtm.android.common_ui.theme.LGTMTheme
import com.lgtm.domain.util.dotStyleDateFormatter
import com.lgtm.domain.util.korean12HourTimeFormatter
import java.time.LocalDateTime

@Composable
fun DateTimeAnnotatedString(
    localDateTime: LocalDateTime?
): AnnotatedString {
     return when (localDateTime) {
        null -> AnnotatedString("-")

        else -> {
            val time = localDateTime.format(korean12HourTimeFormatter)
            val date = localDateTime.format(dotStyleDateFormatter)
            buildAnnotatedString {
                withStyle(style = SpanStyle(
                    color = LGTMTheme.colors.gray_5,
                    fontStyle = LGTMTheme.typography.description.fontStyle
                    )
                ) {
                    append(date)
                }

                withStyle(style = SpanStyle(
                    color = LGTMTheme.colors.gray_3,
                    fontStyle = LGTMTheme.typography.description.fontStyle
                )) {
                    append(" | ")
                }

                withStyle(style = SpanStyle(
                    color = LGTMTheme.colors.gray_5,
                    fontStyle = LGTMTheme.typography.description.fontStyle
                )
                ) {
                    append(time)
                }
            }
        }
    }
}