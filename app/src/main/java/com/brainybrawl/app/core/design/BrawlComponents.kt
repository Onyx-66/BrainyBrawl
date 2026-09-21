package com.brainybrawl.app.core.design

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.brainybrawl.app.ui.theme.*

enum class ActionTone { PRIMARY, POSITIVE, DESTRUCTIVE, REWARD }

@Composable
fun BrawlButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    enabled: Boolean = true, tone: ActionTone = ActionTone.PRIMARY) {
    val color = when (tone) {
        ActionTone.PRIMARY -> MaterialTheme.colorScheme.primary
        ActionTone.POSITIVE -> Positive
        ActionTone.DESTRUCTIVE -> Negative
        ActionTone.REWARD -> Gold
    }
    Button(onClick, modifier.defaultMinSize(minHeight = 52.dp).background(
        Brush.verticalGradient(listOf(color.copy(alpha=if(enabled)1f else .3f),
            color.copy(red=color.red*.8f,green=color.green*.8f,blue=color.blue*.8f,alpha=if(enabled)1f else .3f))),RoundedCornerShape(14.dp)), enabled,
        shape = RoundedCornerShape(14.dp),
        border=BorderStroke(1.dp,Color.White.copy(alpha=if(enabled).18f else .05f)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent,disabledContainerColor=Color.Transparent,
            contentColor = if (tone == ActionTone.PRIMARY) Color.White else Ink),
        elevation = null) {
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun BrawlPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier, shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = .45f))) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
fun ResourceChip(label: String, value: String, tint: Color, modifier: Modifier = Modifier, icon:Int?=null) {
    Surface(modifier.semantics(mergeDescendants = true) {},
        color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(30.dp)) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            icon?.let{androidx.compose.foundation.Image(androidx.compose.ui.res.painterResource(it),null,Modifier.size(22.dp))}
            Text(label, color = tint, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun AnswerOption(label: String, selected: Boolean, enabled: Boolean,
    onClick: () -> Unit, modifier: Modifier = Modifier, result:Boolean?=null) {
    Surface(modifier.fillMaxWidth().defaultMinSize(minHeight = 52.dp)
        .semantics { role = Role.Checkbox; this.selected = selected }
        .clickable(enabled = enabled, onClick = onClick),
        color = when(result){true->Positive;false->Negative;null->if(selected)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant},
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if(selected)Cyan else MaterialTheme.colorScheme.outline.copy(alpha=.45f))) {
        Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(if (selected) "\u2713" else "\u25cb", modifier = Modifier.clearAndSetSemantics {})
            Text(label, style = MaterialTheme.typography.bodyLarge,color=if(result!=null)Ink else Color.Unspecified)
        }
    }
}

@Composable
fun TimerBar(label: String, fraction: Float, modifier: Modifier = Modifier) {
    Column(modifier.semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, modifier=Modifier.semantics{liveRegion=LiveRegionMode.Polite}, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelLarge)
        LinearProgressIndicator(progress = { fraction.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(6.dp).clearAndSetSemantics{}, color = Gold,
            trackColor = MaterialTheme.colorScheme.surfaceVariant)
    }
}

@Composable
fun PlayerCard(name: String, detail: String, initials: String, modifier: Modifier = Modifier) {
    BrawlPanel(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primary) {
                Text(initials, Modifier.padding(15.dp).clearAndSetSemantics {}, style = MaterialTheme.typography.titleLarge)
            }
            Column { Text(name, style = MaterialTheme.typography.titleMedium)
                Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

@Composable
fun FeedbackPanel(title: String, message: String, action: String? = null, onAction: () -> Unit = {}) {
    BrawlPanel(Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(message, style = MaterialTheme.typography.bodyLarge)
        if (action != null) BrawlButton(action, onAction, Modifier.fillMaxWidth())
    }
}

/** The accessible close zone stays outside the image; tapping the dim area closes. */
@Composable
fun ImageViewer(closeLabel: String, onDismiss: () -> Unit, image: @Composable () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth=false)) {
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .8f)),contentAlignment=Alignment.Center){
            Box(Modifier.matchParentSize().clickable(onClick=onDismiss))
            Column(Modifier.fillMaxWidth().padding(12.dp),horizontalAlignment=Alignment.CenterHorizontally){
                Surface(border=BorderStroke(2.dp,Cyan),shape=RoundedCornerShape(12.dp),
                    modifier=Modifier.fillMaxWidth().pointerInput(Unit){detectTapGestures(onTap={})}){image()}
                Spacer(Modifier.height(16.dp))
                BrawlButton(closeLabel,onDismiss,Modifier.fillMaxWidth(),tone=ActionTone.POSITIVE)
            }
        }
    }
}
