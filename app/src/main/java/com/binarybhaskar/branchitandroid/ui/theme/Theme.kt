package com.binarybhaskar.branchitandroid.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.binarybhaskar.gdg_ggv.ui.theme.DarkWhiteTextColor
import com.binarybhaskar.gdg_ggv.ui.theme.ErrorContainerDark
import com.binarybhaskar.gdg_ggv.ui.theme.ErrorContainerLight
import com.binarybhaskar.gdg_ggv.ui.theme.ErrorDark
import com.binarybhaskar.gdg_ggv.ui.theme.ErrorLight
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleBackgroundDark
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleBackgroundLight
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleBlue
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleBlueOnPrimaryContainerLight
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleBlueOnSecondaryContainerLight
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleBluePrimaryContainerLight
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleBlueSecondaryContainerLight
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleBlueSecondaryLight
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleBlueSurfaceLight
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleBlueSurfaceVariantLight
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleGreen
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleGreenContainerDark
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleGreenContainerLight
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleGreenDark
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleGreenOnTertiaryContainerDark
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleGreenOnTertiaryContainerLight
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleYellow
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleYellowDarker
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleYellowOnPrimaryContainerDark
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleYellowOnSecondaryContainerDark
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleYellowPrimaryContainerDark
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleYellowSecondaryContainerDark
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleYellowSurfaceDark
import com.binarybhaskar.gdg_ggv.ui.theme.GoogleYellowSurfaceVariantDark
import com.binarybhaskar.gdg_ggv.ui.theme.InverseOnSurfaceDark
import com.binarybhaskar.gdg_ggv.ui.theme.InverseOnSurfaceLight
import com.binarybhaskar.gdg_ggv.ui.theme.InversePrimaryDark
import com.binarybhaskar.gdg_ggv.ui.theme.InversePrimaryLight
import com.binarybhaskar.gdg_ggv.ui.theme.InverseSurfaceDark
import com.binarybhaskar.gdg_ggv.ui.theme.InverseSurfaceLight
import com.binarybhaskar.gdg_ggv.ui.theme.LightBlackTextColor
import com.binarybhaskar.gdg_ggv.ui.theme.OnErrorContainerDark
import com.binarybhaskar.gdg_ggv.ui.theme.OnErrorContainerLight
import com.binarybhaskar.gdg_ggv.ui.theme.OnErrorDark
import com.binarybhaskar.gdg_ggv.ui.theme.OnErrorLight
import com.binarybhaskar.gdg_ggv.ui.theme.OutlineDark
import com.binarybhaskar.gdg_ggv.ui.theme.OutlineLight
import com.binarybhaskar.gdg_ggv.ui.theme.OutlineVariantDark
import com.binarybhaskar.gdg_ggv.ui.theme.OutlineVariantLight
import com.binarybhaskar.gdg_ggv.ui.theme.Scrim
import com.binarybhaskar.gdg_ggv.ui.theme.TextBlackOnBackgroundLight
import com.binarybhaskar.gdg_ggv.ui.theme.TextWhiteOnBackgroundDark

private val DarkColorScheme = darkColorScheme(
    primary = GoogleYellow,
    onPrimary = LightBlackTextColor,
    primaryContainer = GoogleYellowPrimaryContainerDark,
    onPrimaryContainer = GoogleYellowOnPrimaryContainerDark,

    secondary = GoogleYellowDarker,
    onSecondary = LightBlackTextColor,
    secondaryContainer = GoogleYellowSecondaryContainerDark,
    onSecondaryContainer = GoogleYellowOnSecondaryContainerDark,

    tertiary = GoogleGreenDark,
    onTertiary = LightBlackTextColor,
    tertiaryContainer = GoogleGreenContainerDark,
    onTertiaryContainer = GoogleGreenOnTertiaryContainerDark,

    background = GoogleBackgroundDark,
    onBackground = TextWhiteOnBackgroundDark,

    surface = GoogleYellowSurfaceDark,
    onSurface = DarkWhiteTextColor,
    surfaceVariant = GoogleYellowSurfaceVariantDark,
    onSurfaceVariant = TextWhiteOnBackgroundDark,

    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,

    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,

    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inversePrimary = InversePrimaryDark,

    scrim = Scrim
)

private val LightColorScheme = lightColorScheme(
    primary = GoogleBlue,
    onPrimary = DarkWhiteTextColor,
    primaryContainer = GoogleBluePrimaryContainerLight,
    onPrimaryContainer = GoogleBlueOnPrimaryContainerLight,

    secondary = GoogleBlueSecondaryLight,
    onSecondary = DarkWhiteTextColor,
    secondaryContainer = GoogleBlueSecondaryContainerLight,
    onSecondaryContainer = GoogleBlueOnSecondaryContainerLight,

    tertiary = GoogleGreen,
    onTertiary = DarkWhiteTextColor,
    tertiaryContainer = GoogleGreenContainerLight,
    onTertiaryContainer = GoogleGreenOnTertiaryContainerLight,

    background = GoogleBackgroundLight,
    onBackground = TextBlackOnBackgroundLight,

    surface = GoogleBlueSurfaceLight,
    onSurface = LightBlackTextColor,
    surfaceVariant = GoogleBlueSurfaceVariantLight,
    onSurfaceVariant = TextBlackOnBackgroundLight,

    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,

    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,

    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    inversePrimary = InversePrimaryLight,

    scrim = Scrim
)

@Composable
fun BranchITTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}