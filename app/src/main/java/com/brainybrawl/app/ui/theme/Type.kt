package com.brainybrawl.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import com.brainybrawl.app.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Poppins matches the reference typography; Android provides fallback Arabic glyphs.
// Bundled under SIL OFL: assets/licenses/Poppins-OFL.txt.
private val BrawlFont=FontFamily(Font(R.font.poppins_regular),Font(R.font.poppins_bold,FontWeight.Bold),Font(R.font.poppins_extrabold,FontWeight.ExtraBold))
val Typography = Typography(
    headlineLarge = TextStyle(fontFamily = BrawlFont, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, lineHeight = 38.sp),
    headlineMedium = TextStyle(fontFamily = BrawlFont, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp),
    headlineSmall = TextStyle(fontFamily=BrawlFont,fontWeight=FontWeight.Bold,fontSize=23.sp,lineHeight=30.sp),
    titleSmall = TextStyle(fontFamily=BrawlFont,fontWeight=FontWeight.Bold,fontSize=14.sp,lineHeight=20.sp),
    labelMedium = TextStyle(fontFamily=BrawlFont,fontWeight=FontWeight.Bold,fontSize=12.sp,lineHeight=18.sp),
    titleLarge = TextStyle(fontFamily = BrawlFont, fontWeight = FontWeight.Bold, fontSize = 21.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = BrawlFont, fontWeight = FontWeight.Bold, fontSize = 17.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontFamily = BrawlFont, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = BrawlFont, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp),
    labelLarge = TextStyle(fontFamily = BrawlFont, fontWeight = FontWeight.Bold, fontSize = 15.sp, lineHeight = 20.sp)
)
