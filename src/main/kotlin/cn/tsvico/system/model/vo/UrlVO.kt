package cn.tsvico.system.model.vo

class UrlVO(var url: String?, var method: String?, var type: String?) {
    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val urlVO = o as UrlVO
        return if (url != urlVO.url) {
            false
        } else method == urlVO.method && type == urlVO.type
    }

    override fun hashCode(): Int {
        var result = if (url != null) url.hashCode() else 0
        result = 31 * result + if (method != null) method.hashCode() else 0
        result = 31 * result + if (type != null) type.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "UrlVO{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", type='" + type + '\'' +
                '}'
    }
}