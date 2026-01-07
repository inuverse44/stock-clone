package inuverse

import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.jboss.logging.Logger

@ApplicationScoped
class JetStreamListener {

    private val log = Logger.getLogger(this::class.java)

    fun onStart(@Observes staruptEvent: StartupEvent) {
        log.info("JetStreamListener is starting...")
    }

    fun onStop(@Observes shutdownEvent: ShutdownEvent) {
        log.info("JetStreamListener is being shutdown...")
    }

}