package im.zhaojun.common.exception

class UnknownRedirectUrlException(message: String?) : RuntimeException(message) {
    companion object {
        private const val serialVersionUID = -4511193905202048700L
    }
}