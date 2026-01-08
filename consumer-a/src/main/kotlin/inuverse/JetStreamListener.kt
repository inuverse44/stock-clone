package inuverse

import io.minio.MinioClient
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.jboss.logging.Logger

@ApplicationScoped
class JetStreamListener(
    private val minioClient: MinioClient,
    private val objectMapper: com.fasterxml.jackson.databind.ObjectMapper
) {

    private val log = Logger.getLogger(this::class.java)

    @Incoming("minio-events")
    fun process(payload: String) {
        try {
            val event = objectMapper.readValue(payload, MinioEvent::class.java)
            log.info("Received event from MinIO: $event")
            
            // Key might be "stock-data/filename.gz"
            val fullKey = event.key
            if (fullKey.isNullOrBlank()) {
                log.warn("Key is null or empty, skipping.")
                return
            }

            // 簡易的な処理: ファイル名のみ抽出
            val fileName = fullKey.substringAfterLast("/")
            log.info("Detected file upload: $fileName (Full Key: $fullKey)")
            
            // TODO: ここでMinIOからダウンロード処理を行う
        } catch (e: Exception) {
            log.error("Failed to process message: $payload", e)
        }
    }
}