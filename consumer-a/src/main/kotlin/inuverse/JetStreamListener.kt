package inuverse

import com.fasterxml.jackson.databind.ObjectMapper
import io.minio.MinioClient
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Message
import org.jboss.logging.Logger
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletionStage

@ApplicationScoped
class JetStreamListener(
    private val minioClient: MinioClient,
    private val objectMapper: ObjectMapper
) {

    private val log = Logger.getLogger(this::class.java)

    @Incoming("minio-events")
    fun process(message: Message<ByteArray>): CompletionStage<Void> {
        // 生のバイト配列として受け取ることで、ヘッダーチェックを回避
        val payload = String(message.payload, StandardCharsets.UTF_8)
        
        try {
            val event = objectMapper.readValue(payload, MinioEvent::class.java)
            log.info("Received event from MinIO: $event")

            val fullKey = event.key
            if (fullKey.isNullOrBlank()) {
                log.warn("Key is null or empty, skipping.")
            } else {
                val fileName = fullKey.substringAfterLast("/")
                log.info("Detected file upload: $fileName (Full Key: $fullKey)")
                
                // TODO: ダウンロード処理
            }
        } catch (e: Exception) {
            log.error("Failed to process message: $payload", e)
        }
        
        // 処理完了を通知 (ACK)
        return message.ack()
    }
}