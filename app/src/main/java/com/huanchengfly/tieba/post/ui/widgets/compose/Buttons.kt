package com.huanchengfly.tieba.post.ui.widgets.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.huanchengfly.tieba.post.ui.common.theme.compose.ExtendedTheme

@Composable
fun DefaultButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(100),
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = ExtendedTheme.colorScheme.primary,
        contentColor = ExtendedTheme.colorScheme.onAccent
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick,
        modifier,
        enabled,
        shape,
        colors,
        elevation,
        border,
        contentPadding,
        interactionSource,
        content
    )
}

@Composable
fun TextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(100),
    color: Color = ExtendedTheme.colorScheme.text,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val disabledAlpha = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f).alpha
    DefaultButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = shape,
        border = border,
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.1f),
            contentColor = color,
            disabledContainerColor = color.copy(alpha = disabledAlpha * 0.1f),
            disabledContentColor = color.copy(alpha = disabledAlpha)
        ),
        contentPadding = contentPadding,
        content = content
    )
}