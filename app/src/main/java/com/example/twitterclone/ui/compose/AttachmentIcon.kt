package com.example.twitterclone.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun rememberAttachment(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "attachment",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12.375f, 29.042f)
                quadToRelative(-3.75f, 0f, -6.375f, -2.667f)
                reflectiveQuadToRelative(-2.625f, -6.417f)
                quadToRelative(0f, -3.791f, 2.604f, -6.437f)
                quadToRelative(2.604f, -2.646f, 6.354f, -2.646f)
                horizontalLineToRelative(17.875f)
                quadToRelative(2.667f, 0f, 4.563f, 1.896f)
                reflectiveQuadToRelative(1.896f, 4.604f)
                quadToRelative(0f, 2.667f, -1.896f, 4.583f)
                quadToRelative(-1.896f, 1.917f, -4.563f, 1.917f)
                horizontalLineTo(13.875f)
                quadToRelative(-1.625f, 0f, -2.75f, -1.125f)
                reflectiveQuadTo(10f, 20f)
                quadToRelative(0f, -1.625f, 1.146f, -2.75f)
                reflectiveQuadToRelative(2.812f, -1.125f)
                horizontalLineToRelative(15.334f)
                quadToRelative(0.375f, 0f, 0.646f, 0.292f)
                quadToRelative(0.27f, 0.291f, 0.27f, 0.666f)
                quadToRelative(0f, 0.375f, -0.27f, 0.646f)
                quadToRelative(-0.271f, 0.271f, -0.646f, 0.271f)
                horizontalLineTo(13.917f)
                quadToRelative(-0.834f, 0f, -1.438f, 0.583f)
                quadToRelative(-0.604f, 0.584f, -0.604f, 1.417f)
                quadToRelative(0f, 0.833f, 0.583f, 1.417f)
                quadToRelative(0.584f, 0.583f, 1.417f, 0.583f)
                horizontalLineToRelative(16.333f)
                quadToRelative(1.917f, 0f, 3.25f, -1.354f)
                quadToRelative(1.334f, -1.354f, 1.334f, -3.271f)
                quadToRelative(0f, -1.958f, -1.334f, -3.312f)
                quadToRelative(-1.333f, -1.355f, -3.25f, -1.355f)
                horizontalLineTo(12.292f)
                quadToRelative(-2.959f, 0f, -5.021f, 2.125f)
                quadToRelative(-2.063f, 2.125f, -2.063f, 5.125f)
                reflectiveQuadToRelative(2.084f, 5.104f)
                quadToRelative(2.083f, 2.105f, 5.083f, 2.105f)
                horizontalLineToRelative(16.917f)
                quadToRelative(0.375f, 0f, 0.646f, 0.271f)
                quadToRelative(0.27f, 0.27f, 0.27f, 0.645f)
                quadToRelative(0f, 0.417f, -0.27f, 0.688f)
                quadToRelative(-0.271f, 0.271f, -0.646f, 0.271f)
                close()
            }
        }.build()
    }
}