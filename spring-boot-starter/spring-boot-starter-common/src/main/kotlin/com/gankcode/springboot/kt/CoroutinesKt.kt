package library.kt

import com.gankcode.springboot.kt.log
import kotlinx.coroutines.*
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

val UI = Dispatchers.Main.immediate
val IO = Dispatchers.IO
val ErrorHandler = UncaughtScopeExceptionHandler()


fun uiScope(): CoroutineScope = CoroutineScope(
    Job() + UI + ErrorHandler
)

fun ioScope(): CoroutineScope = CoroutineScope(
    Job() + IO + ErrorHandler
)

fun uiSupervisorScope(): CoroutineScope = CoroutineScope(
    SupervisorJob() + UI + ErrorHandler
)

fun ioSupervisorScope(): CoroutineScope = CoroutineScope(
    SupervisorJob() + IO + ErrorHandler
)

class UncaughtScopeExceptionHandler(
    private val errorHandler: ((Throwable) -> Unit)? = null
) : CoroutineExceptionHandler, AbstractCoroutineContextElement(CoroutineExceptionHandler.Key) {

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        if (errorHandler != null) {
            errorHandler.invoke(exception)
        } else {
            log.error("coroutine scope error: ", exception)
        }
    }

}