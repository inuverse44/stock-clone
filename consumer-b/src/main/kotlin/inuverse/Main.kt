package inuverse

import io.quarkus.logging.Log
import io.quarkus.runtime.Quarkus
import io.quarkus.runtime.annotations.QuarkusMain
import java.util.TimeZone

@QuarkusMain
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"))
        Log.info("======== START ${this::class.java} =========")
        Quarkus.run(*args)
    }
}
