package cn.tsvico.common.exception

class TreeCastException(cause: Throwable?) : RuntimeException(cause) {
    companion object {
        private const val serialVersionUID = -7358633666514111106L
    }
}