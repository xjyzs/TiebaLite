package com.huanchengfly.tieba.post.ui.widgets.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.material3.fade
import io.github.fornewid.placeholder.material3.placeholder
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme

@Composable
fun UserHeaderPlaceholder(
    avatarSize: Dp
) {
    UserHeader(
        avatar = {
            AvatarPlaceholder(avatarSize)
        },
        name = {
            Text(
                text = "Username",
                modifier = Modifier.placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.fade(),
                )
            )
        },
        desc = {
            Text(
                text = "Desc",
                modifier = Modifier.placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.fade(),
                )
            )
        }
    )
}

@Composable
fun UserHeader(
    avatar: @Composable () -> Unit,
    name: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    desc: @Composable (() -> Unit)? = null,
    content: @Composable (RowScope.() -> Unit)? = null,
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    } else Modifier
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = clickableModifier,
        ) {
            avatar()
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            ProvideTextStyle(
                value = MaterialTheme.typography.titleMedium.merge(
                    TextStyle(
                        color = ExtendedTheme.colorScheme.text,
                        fontSize = 13.sp
                    )
                )
            ) {
                name()
            }
            if (desc != null) {
                ProvideTextStyle(
                    value = MaterialTheme.typography.bodySmall.merge(
                        TextStyle(
                            color = ExtendedTheme.colorScheme.textSecondary,
                            fontSize = 11.sp
                        )
                    )
                ) {
                    desc()
                }
            }
        }
        content?.invoke(this)
    }
}