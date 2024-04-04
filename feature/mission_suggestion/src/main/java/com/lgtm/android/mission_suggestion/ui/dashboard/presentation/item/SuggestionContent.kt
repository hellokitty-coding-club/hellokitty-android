package com.lgtm.android.mission_suggestion.ui.dashboard.presentation.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lgtm.android.common_ui.components.buttons.LikeButton
import com.lgtm.android.common_ui.components.texts.DateTimeAnnotatedString
import com.lgtm.android.common_ui.theme.LGTMTheme
import com.lgtm.android.common_ui.util.throttleClickable
import com.lgtm.domain.mission_suggestion.SuggestionVO

@Composable
fun SuggestionContent(
    index: Int,
    suggestionVO: SuggestionVO,
    onSuggestionClick: (Int, SuggestionVO) -> Unit,
    onSuggestionLike: (Int, Int) -> Unit,
    onSuggestionCancelLike: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = LGTMTheme.colors.white,
                shape = RoundedCornerShape(20.dp),
            )
            .border(
                width = 1.dp,
                color = LGTMTheme.colors.gray_2,
                shape = RoundedCornerShape(20.dp),
            )
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
            .throttleClickable(
                enabled = true,
                onClick = { onSuggestionClick(suggestionVO.suggestionId, suggestionVO) }
            )
    ) {
        Text(
            text = suggestionVO.title,
            color = LGTMTheme.colors.black,
            style = LGTMTheme.typography.body2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Divider(
            modifier = Modifier
                .padding(top = 16.dp)
                .background(color = LGTMTheme.colors.gray_2)
        )

        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = suggestionVO.description,
            color = LGTMTheme.colors.black,
            style = LGTMTheme.typography.body3R,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = DateTimeAnnotatedString(localDateTime = suggestionVO.dateTime))
            LikeButton(likeNum = suggestionVO.likeNum, isLiked = suggestionVO.isLiked) {
                if (suggestionVO.isLiked) {
                    onSuggestionCancelLike(index, suggestionVO.suggestionId)
                } else {
                    onSuggestionLike(index, suggestionVO.suggestionId)
                }
            }
        }

    }
}