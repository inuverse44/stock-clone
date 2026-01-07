package inuverse

import org.jboss.logging.Logger
import io.smallrye.mutiny.Multi
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import java.util.UUID


@Path("/quotes") // このクラス全体の親パスを定義。よって http://localhost:8080/quotes のように始まる
class QuoteResource(
    @Channel("quote-requests")
    private val quoteRequestEmitter: Emitter<String>,
    @Channel("quotes")
    private val quotes: Multi<Quote>
) {
    private val log = Logger.getLogger(this::class.java)

    @POST //  curl -X POSTが必要
    @Path("/request") // メソッドレベルの子パス
    @Produces(MediaType.TEXT_PLAIN)
    fun createRequest(): String = UUID.randomUUID()
        .toString().also { quoteRequestEmitter.send(it) }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    fun stream(): Multi<Quote> {
        log.info("stream is called: $quotes")
        return quotes
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    fun sayHello(): String {
        log.info("sayHello is called: $quotes")
        return "Hello from Quotes!"
    }
}