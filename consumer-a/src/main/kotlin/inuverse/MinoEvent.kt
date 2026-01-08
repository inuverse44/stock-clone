package inuverse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class MinioEvent(
    @param:JsonProperty("Key") val key: String?,
    @param:JsonProperty("EventName") val eventName: String?
)

