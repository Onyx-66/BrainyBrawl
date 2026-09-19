package com.brainybrawl.app.game.engine

import com.brainybrawl.app.game.content.QuestionContent

/** Monotonic deadlines survive recomposition, rotation and backgrounding. */
data class OfflineQuestions(val questions:List<QuestionContent>,val index:Int=0,val score:Int=0,
    val startedAt:Long,val selected:String?=null,val revealed:Boolean=false) {
    init { require(questions.isNotEmpty() && index in questions.indices) }
    val question get()=questions[index]
    val window get()=RoundWindow(startedAt,startedAt,startedAt+45_000)
    val finished get()=revealed && index==questions.lastIndex
    fun tick(now:Long)=if(now>=window.deadline)copy(revealed=true) else this
    fun answer(id:String,now:Long):OfflineQuestions {
        if(revealed || selected!=null || !window.accepting(now))return tick(now)
        val option=question.options.single{it.id==id}
        return copy(selected=id,score=score+if(option.correct)1 else 0,revealed=true)
    }
    fun answerText(text:String,now:Long):OfflineQuestions {
        if(revealed||selected!=null||!window.accepting(now))return tick(now)
        val accepted=question.acceptedAnswers+question.options.single{it.correct}.label
        val correct=accepted.any{normalizeAnswer(it)==normalizeAnswer(text)}
        return copy(selected=if(correct)question.options.single{it.correct}.id else "typed_wrong",score=score+if(correct)1 else 0,revealed=true)
    }
    fun next(now:Long):OfflineQuestions {
        require(revealed && !finished)
        return copy(index=index+1,startedAt=now,selected=null,revealed=false)
    }
}
