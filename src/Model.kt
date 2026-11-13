enum class ExitCode(val value: Int) {
    SUCCESS(0), HELP(1), WRONG_PASSWORD(2), WRONG_LOGIN(3), UNKNOWN_ACTION(4),
    ACCESS_DENIED(5), RESOURCE_NOT_FOUND(6), INVALID_FORMAT(7), VOLUME_EXCEEDED(8)
}

enum class Action(val flag: String) {
    READ("read"), WRITE("write"), EXECUTE("execute");

    companion object {
        fun fromFlag(value: String): Action? = values().firstOrNull { it.flag == value }
    }
}

data class Resource(val path: String, val maxVolume: Int)

// Raw strings are intentional: semantic errors must return codes 4 or 7 silently.
data class Request(
    val login: String,
    val password: String,
    val action: String,
    val resource: String,
    val volume: String
)

object ResourceCatalog {
    val resources: Map<String, Resource> = listOf(
        Resource("A", 1000),
        Resource("A.B", 300),
        Resource("A.B.C", 100),
        Resource("A.B.D", 50),
        Resource("A.Beta", 60),
        Resource("A.Tools", 200),
        Resource("A.Tools.Runner", 20),
        Resource("A.A8B", 250),
        Resource("A.A8B.C", 150),
        Resource("A.A8B.C.f_d", 40),
        Resource("Public", 500),
        Resource("Public.Docs", 200),
        Resource("Secret", 100)
    ).associateBy { it.path }

    private val pathPattern = Regex("[A-Za-z0-9_]{1,20}(\\.[A-Za-z0-9_]{1,20})*")
    private val volumePattern = Regex("[0-9]+")

    fun isValidPath(path: String): Boolean = pathPattern.matches(path)

    // Volume is a nonnegative 32-bit integer, including zero.
    fun parseVolume(value: String): Int? =
        if (volumePattern.matches(value)) value.toIntOrNull() else null
}
