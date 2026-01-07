package inuverse

import io.smallrye.mutiny.Multi
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import java.util.UUID


@Path("/quotes")
class QuoteResource(
    @Channel("quote-requests")
    private val quoteRequestEmitter: Emitter<String>,
    @Channel("quotes")
    private val quotes: Multi<Quote>
) {
    @POST
    @Path("/request")
    @Produces(MediaType.TEXT_PLAIN)
    fun createRequest(): String = UUID.randomUUID()
        .toString().also { quoteRequestEmitter.send(it) }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    fun stream(): Multi<Quote> = quotes
}