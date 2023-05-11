package com.kodex.yogamusic.ui.theme

import androidx.compose.ui.graphics.Color
import com.google.firebase.annotations.concurrent.Background

val dialogColor = Color(0xFF5F5F5F)

sealed class ThemeColors(
    val background: Color,
    val surface: Color,
    val primary : Color,
    val text: Color
){
    object Dark : ThemeColors(
        background = Color(0xFF000000),
        surface = Color(0xFF000000),
        primary = Color(0xFF4FB64C),
        text = Color.White
    )
}