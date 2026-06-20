package br.com.mauricio.oconcurseiro.data.analytics

import android.content.Context
import android.os.Build
import android.util.Log
import br.com.mauricio.oconcurseiro.data.remote.AnalyticsEventRequestDto
import br.com.mauricio.oconcurseiro.data.remote.ConcurseiroApi
import br.com.mauricio.oconcurseiro.domain.model.FiltroParams
import br.com.mauricio.oconcurseiro.domain.model.Questao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsTracker @Inject constructor(@ApplicationContext private val context: Context, private val api: ConcurseiroApi, private val sessions: SessionManager) {
    private val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
    private val preferences=context.getSharedPreferences("analytics_preferences",Context.MODE_PRIVATE)
    private val anonymousId=preferences.getString(ANONYMOUS_ID_KEY,null) ?: UUID.randomUUID().toString().also{preferences.edit().putString(ANONYMOUS_ID_KEY,it).apply()}
    private val appVersion by lazy { runCatching{context.packageManager.getPackageInfo(context.packageName,0).versionName}.getOrNull()?:"unknown" }

    fun appOpened()=track(AnalyticsEvent(AnalyticsEventName.APP_OPENED))
    fun startSession(){if(sessions.active)return;sessions.start();track(AnalyticsEvent(AnalyticsEventName.SESSION_STARTED))}
    fun stopSession(){if(!sessions.active)return;val summary=sessions.finish();track(AnalyticsEvent(AnalyticsEventName.SESSION_ENDED,metadata=summary))}
    fun screenViewed(name:String){sessions.screenViewed();track(AnalyticsEvent(AnalyticsEventName.SCREEN_VIEWED,screenName=name,metadata=mapOf("route" to name)))}
    fun questionViewed(q:Questao)=track(questionEvent(AnalyticsEventName.QUESTION_VIEWED,q))
    fun questionAnswered(q:Questao,correct:Boolean){sessions.questionAnswered();track(questionEvent(AnalyticsEventName.QUESTION_ANSWERED,q).copy(answerCorrect=correct,metadata=mapOf("correct" to correct)))}
    fun filtersApplied(f:FiltroParams){
        val active=buildList{if(!f.texto.isNullOrBlank())add("texto");if(f.disciplinaId!=null)add("disciplina");if(f.assuntoId!=null||!f.assuntoIds.isNullOrEmpty())add("assunto");if(f.subassuntoId!=null||!f.subassuntoIds.isNullOrEmpty())add("subassunto");if(f.bancaId!=null)add("banca");if(f.instituicaoId!=null)add("instituicao");if(f.ano!=null)add("ano");if(!f.cargo.isNullOrBlank())add("cargo");if(!f.nivel.isNullOrBlank())add("nivel");if(!f.modalidade.isNullOrBlank())add("modalidade")}
        (active.ifEmpty{listOf("sem_filtros")}).forEach{name->track(AnalyticsEvent(AnalyticsEventName.FILTER_APPLIED,"filtro",name,disciplinaId=f.disciplinaId,assuntoId=f.assuntoId,subassuntoId=f.subassuntoId,bancaId=f.bancaId,instituicaoId=f.instituicaoId,metadata=mapOf("filter_name" to name)))}
        f.texto?.takeIf { it.isNotBlank() }?.let { query -> track(AnalyticsEvent(AnalyticsEventName.SEARCH_PERFORMED,"filtro",metadata=mapOf("query_length" to query.length))) }
    }
    fun emptyResult(screen:String)=track(AnalyticsEvent(AnalyticsEventName.EMPTY_RESULT_VIEWED,screen,metadata=mapOf("has_results" to false)))
    fun error(type:String,screen:String?=null)=track(AnalyticsEvent(AnalyticsEventName.ERROR_OCCURRED,screen,metadata=mapOf("error_type" to type.take(80))))

    private fun questionEvent(name:AnalyticsEventName,q:Questao)=AnalyticsEvent(name,"questao",questionId=q.id,disciplinaId=q.disciplinaId,assuntoId=q.assuntoId,bancaId=q.bancaId,instituicaoId=q.orgaoId,metadata=mapOf("question_id" to q.id))
    fun track(event:AnalyticsEvent){val sessionId=sessions.sessionId;scope.launch{runCatching{api.registrarEventoAnalytics(AnalyticsEventRequestDto(event.name.wireName,anonymousId,null,sessionId,event.screenName,event.filterName,event.questionId,event.answerCorrect,event.disciplinaId,event.assuntoId,event.subassuntoId,null,appVersion,"android",Build.VERSION.RELEASE,1,event.bancaId,event.instituicaoId,event.provaId,event.metadata))}.onSuccess{Log.d(TAG,"Evento enviado: ${event.name.wireName}")}.onFailure{Log.w(TAG,"Falha em ${event.name.wireName}: ${it.javaClass.simpleName}")}}}
    private companion object{const val TAG="ConcurseiroAnalytics";const val ANONYMOUS_ID_KEY="anonymous_id_v1"}
}
