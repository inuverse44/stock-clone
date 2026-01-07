package inuverse


import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class Quote(
    val id: String = "",
    val price: Int = 0
)