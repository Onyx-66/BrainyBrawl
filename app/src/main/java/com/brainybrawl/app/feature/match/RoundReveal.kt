package com.brainybrawl.app.feature.match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brainybrawl.app.R
import com.brainybrawl.app.core.design.*
import com.brainybrawl.app.core.localization.namedString
import kotlinx.serialization.json.*

/** Only closed-round snapshots may reveal correctness or hidden weights. */
@Composable fun RoundReveal(round:MatchRound){
    if(round.status!="results")return
    val reveal=round.reveal?:return
    Column(Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(8.dp)){
        Text(round.content.prompt,style=MaterialTheme.typography.titleMedium)
        val submitted=round.submission?.action
        if(round.kind=="image_guess"){
            val selected=submitted?.get("choice_ids")?.jsonArray?.map{it.jsonPrimitive.content}.orEmpty()
            reveal["choices"]?.jsonArray?.forEach{entry->
                val choice=entry.jsonObject
                val id=choice["id"]?.jsonPrimitive?.contentOrNull
                val option=round.content.options.find{it.id==id}
                val correct=choice["correct"]?.jsonPrimitive?.booleanOrNull
                val points=choice["points"]?.jsonPrimitive?.intOrNull
                if(option!=null&&correct!=null&&points!=null)AnswerOption(namedString(R.string.choice_points,"label" to option.label,"points" to points),id in selected,false,{},result=correct)
            }
        }else if(round.kind=="question_round"){
            val correct=reveal["correct_option_id"]?.jsonPrimitive?.contentOrNull
            val selected=submitted?.get("option_id")?.jsonPrimitive?.contentOrNull
            round.content.options.forEach{option->AnswerOption(option.label,option.id==selected,false,{},result=if(option.id==correct)true else if(option.id==selected)false else null)}
        }
        reveal["explanation"]?.jsonPrimitive?.contentOrNull?.takeIf{it.isNotBlank()}?.let{Text(it)}
    }
}
