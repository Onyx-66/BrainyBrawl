package com.brainybrawl.app
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.res.stringResource
import com.brainybrawl.app.core.localization.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
class LocalizationUiTest {
    @get:Rule val rule=createComposeRule()
    @Test fun arabicUsesRtlAndLocalizedNumbers(){
        var direction=LayoutDirection.Ltr
        rule.setContent{LocalizedContent("ar"){
            val current=LocalLayoutDirection.current
            SideEffect{direction=current}
            androidx.compose.foundation.layout.Column {
                Text(stringResource(R.string.home))
                Text(namedString(R.string.session_score,"score" to 123))
            }
        }}
        rule.onNodeWithText("الرئيسية").assertIsDisplayed()
        val digits=java.text.NumberFormat.getNumberInstance(java.util.Locale.forLanguageTag("ar")).format(123)
        rule.onNodeWithText("\u0627\u0644\u0646\u062a\u064a\u062c\u0629 \u2068${digits}\u2069").assertIsDisplayed()
        rule.runOnIdle{assertEquals(LayoutDirection.Rtl,direction)}
    }
    @Test fun frenchUsesMatchingResources(){
        rule.setContent{LocalizedContent("fr"){Text(stringResource(R.string.choose_mode))}}
        rule.onNodeWithText("Choisir un mode").assertIsDisplayed()
    }
}
