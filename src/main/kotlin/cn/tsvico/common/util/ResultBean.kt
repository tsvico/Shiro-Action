package cn.tsvico.common.util

import java.io.Serializable

class ResultBean : Serializable {
    var msg = "操作成功"
    var code = SUCCESS
    var data: Any? = null

    private constructor() : super() {}
    private constructor(msg: String, data: Any?, code: Int) {
        this.msg = msg
        this.data = data
        this.code = code
    }

    companion object {
        private const val serialVersionUID = -8276264968757808344L
        private const val SUCCESS = 0
        const val FAIL = -1
        fun successData(data: Any?): ResultBean {
            return success("操作成功", data)
        }

        fun success(data: Any?): ResultBean {
            return success("操作成功", data)
        }

        @JvmOverloads
        fun success(msg: String = "操作成功", data: Any? = null): ResultBean {
            return ResultBean(msg, data, SUCCESS)
        }

        fun error(msg: String): ResultBean {
            val resultBean = ResultBean()
            resultBean.code = FAIL
            resultBean.msg = msg
            return resultBean
        }
    }
}