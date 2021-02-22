package cn.tsvico.common.exception

class AuthcTypeNotSupportException(message: String?) : RuntimeException(message) {
    companion object {
        private const val serialVersionUID = -8964524099437750622L
    }
}