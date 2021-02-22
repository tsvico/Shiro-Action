package cn.tsvico.common.util

import java.io.Serializable

class PageResultBean<T>(var count: Long, var data: List<T>) : Serializable {
    var code = 0
}