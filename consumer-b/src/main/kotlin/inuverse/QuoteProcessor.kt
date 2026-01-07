package inuverse

import java.util.Random

import jakarta.enterprise.context.ApplicationScoped

import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Outgoing

import io.smallrye.reactive.messaging.annotations.Blocking



@ApplicationScoped
class QuoteProcessor {
    private val random = Random()

    @Incoming("requests")
    @Outgoing("quotes")
    @Blocking
    fun process(quoteRequest: String): Quote {
        Thread.sleep(1000)
        val quote = Quote(quoteRequest, random.nextInt(100))
        return quote
    }
}