//package bot.boobbot.flight
//
//import java.util.concurrent.CompletableFuture
//
//@Suppress("UNCHECKED_CAST")
//class WaitingEvent<T : Event>(
//    private val eventClass: Class<*>,
//    private val predicate: (T) -> Boolean,
//    private val future: CompletableFuture<T?>
//) {
//
//    fun check(event: Event) = eventClass.isAssignableFrom(event::class.java) && predicate(event as T)
//
//    fun accept(event: Event?) = future.complete(event as T)
//
//}
