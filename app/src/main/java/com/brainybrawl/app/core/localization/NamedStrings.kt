package com.brainybrawl.app.core.localization

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

fun formatNamed(template: String, values: Map<String,String>): String =
    Regex("\\{([A-Za-z][A-Za-z0-9_]*)\\}").replace(template) {
        requireNotNull(values[it.groupValues[1]]) { "Missing translation placeholder" }
    }
@Composable
fun namedString(@StringRes id: Int, vararg values: Pair<String,Any>): String {
    val locale=androidx.compose.ui.platform.LocalConfiguration.current.locales[0]
    val formatter=java.text.NumberFormat.getNumberInstance(locale)
    return formatNamed(stringResource(id), values.associate { (key,value) ->
        val display=if(value is Number)formatter.format(value) else value.toString()
        key to if(locale.language=="ar") "\u2068$display\u2069" else display
    })
}
