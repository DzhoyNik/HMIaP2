object AccessControl {
    fun isAllowed(user: User, path: String, action: Action): Boolean {
        var current = path
        while (true) {
            if (action in user.grants[current].orEmpty()) return true
            val separator = current.lastIndexOf('.')
            if (separator < 0) return false
            current = current.substring(0, separator)
        }
    }

    fun evaluate(request: Request): ExitCode {
        val user = Auth.findUser(request.login) ?: return ExitCode.WRONG_LOGIN
        if (!Auth.verifyPassword(user, request.password)) return ExitCode.WRONG_PASSWORD
        val action = Action.fromFlag(request.action) ?: return ExitCode.UNKNOWN_ACTION
        if (!ResourceCatalog.isValidPath(request.resource)) return ExitCode.INVALID_FORMAT
        val volume = ResourceCatalog.parseVolume(request.volume) ?: return ExitCode.INVALID_FORMAT
        val resource = ResourceCatalog.resources[request.resource] ?: return ExitCode.RESOURCE_NOT_FOUND
        if (!isAllowed(user, resource.path, action)) return ExitCode.ACCESS_DENIED
        if (volume > resource.maxVolume) return ExitCode.VOLUME_EXCEEDED
        return ExitCode.SUCCESS
    }
}
