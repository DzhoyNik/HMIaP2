import java.security.MessageDigest

data class User(
    val login: String,
    val saltHex: String,
    val passwordHashHex: String,
    val grants: Map<String, Set<Action>>
)

object Auth {
    // Unique random 16-byte salts and precomputed SHA-256 hashes only.
    private val users = listOf(
        User("alice", "b624d639e0e38a85a2308a7318a9c6b5",
            "bdd19164c6dce5b9af1d6b78701512ff01c0e3d7d0c5374810d254ac8f59ecd9",
            mapOf("A" to setOf(Action.READ), "A.B" to setOf(Action.WRITE), "A.Tools" to setOf(Action.EXECUTE))),
        User("bob", "72dc655744c1d942c861a41f2a69975a",
            "2b34f46f672393ed9cc5d3f5498e31f6a04c9ca93e95c35d1040e7c67265f810",
            mapOf("A.B" to setOf(Action.READ))),
        User("admin", "678a85cb913969b0268c03fe586d8764",
            "760884270efcc163edeb7fa9b2f7097bfef4af983298178694ae2c122cdda64f",
            mapOf("A" to Action.values().toSet(), "Public" to Action.values().toSet(), "Secret" to Action.values().toSet())),
        User("guest", "9e3422346cfd2acea184c4a224e9c18f",
            "de597c765ad2bee959131f57bc1a759db27e12c1a4bfc0650c0da11592eb836c",
            emptyMap())
    ).associateBy { it.login }

    fun findUser(login: String): User? = users[login]

    fun verifyPassword(user: User, password: String): Boolean {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(decodeHex(user.saltHex))
        val actual = digest.digest(password.toByteArray(Charsets.UTF_8))
        return MessageDigest.isEqual(decodeHex(user.passwordHashHex), actual)
    }

    private fun decodeHex(value: String): ByteArray =
        value.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
