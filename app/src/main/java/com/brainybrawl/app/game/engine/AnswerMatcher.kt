package com.brainybrawl.app.game.engine

import java.text.Normalizer
import java.util.Locale

/** Forgiving spelling for authored aliases, not arbitrary substrings or approximate numbers. */
object AnswerMatcher {
 fun normalize(value:String):String=Normalizer.normalize(value,Normalizer.Form.NFKD)
  .replace(Regex("[\\p{M}ـ]"),"").lowercase(Locale.ROOT)
  .map{when(it){in '٠'..'٩'->'0'+(it-'٠');in '۰'..'۹'->'0'+(it-'۰');'ى'->'ي';'ة'->'ه';'’','\''->' ';else->it}}.joinToString("")
  .replace(Regex("[،,;:!?؟\"()\\[\\]]")," ").replace(Regex("\\s+")," ").trim()
 fun matches(input:String,accepted:Collection<String>,otherAnswers:Collection<String> = emptyList()):Boolean {
  if(input.length !in 1..200)return false
  val text=normalize(input);if(text.isBlank())return false
  val aliases=accepted.map(::normalize).filter{it.isNotBlank()}.toSet()
  if(text in aliases)return true
  numberWords[text]?.let{number->if(number !in aliases)return false}
  // Numbers, formulas, chemical symbols and signs cannot be changed by fuzzy matching.
  if(text.any{it.isDigit()}||text.any{!it.isLetter()&&!it.isWhitespace()})return false
  val numeric=aliases.any{it.matches(Regex("-?[0-9]+([.][0-9]+)?"))}
  var best=Int.MAX_VALUE
  for(alias in aliases){
   if(alias.any{!it.isLetter()&&!it.isWhitespace()})continue
   val length=minOf(text.length,alias.length)
   val limit=when{length<4->0;length<5->if(numeric)1 else 0;length<8->1;length<13->2;else->3}
   if(kotlin.math.abs(text.length-alias.length)>limit)continue
   val d=distance(text,alias)
   if(d<=limit)best=minOf(best,d)
  }
  if(best==Int.MAX_VALUE)return false
  // A displayed wrong answer (or a closer spelling of it) must not score as correct.
  return otherAnswers.map(::normalize).none{it !in aliases&&distance(text,it)<=best}
 }
 fun distance(a:String,b:String):Int {
  var previous=IntArray(b.length+1){it};var before=previous
  for(i in 1..a.length){val row=IntArray(b.length+1);row[0]=i
   for(j in 1..b.length){row[j]=minOf(row[j-1]+1,previous[j]+1,previous[j-1]+if(a[i-1]==b[j-1])0 else 1)
    if(i>1&&j>1&&a[i-1]==b[j-2]&&a[i-2]==b[j-1])row[j]=minOf(row[j],before[j-2]+1)
   };before=previous;previous=row
  };return previous[b.length]
 }
}
