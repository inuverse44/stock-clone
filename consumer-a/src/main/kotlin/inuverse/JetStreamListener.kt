package inuverse

import io.minio.MinioClient
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.jboss.logging.Logger
import java.util.concurrent.CompletionStage
import java.util.concurrent.CompletableFuture
import com.fasterxml.jackson.databind.ObjectMapper

@ApplicationScoped
class JetStreamListener(
    private val minioClient: MinioClient,
    private val objectMapper: ObjectMapper
) {

    private val log = Logger.getLogger(this::class.java)

    @Incoming("minio-events")
    fun process(payload: String): CompletionStage<Void> {
        try {
            val event = objectMapper.readValue(payload, MinioEvent::class.java)
            log.info("Received event from MinIO: $event")

            val fullKey = event.key
            if (fullKey.isNullOrBlank()) {
                log.warn("Key is null or empty, skipping.")
            } else {
                val fileName = fullKey.substringAfterLast("/")
                log.info("Detected file upload: $fileName (Full Key: $fullKey)")
            }
        } catch (e: Exception) {
            log.error("Failed to process message: $payload", e)
        }
        return CompletableFuture.completedFuture(null)
    }
}
