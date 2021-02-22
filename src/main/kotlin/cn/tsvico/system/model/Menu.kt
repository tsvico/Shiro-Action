package cn.tsvico.system.model

import com.fasterxml.jackson.annotation.JsonIgnore
import cn.tsvico.system.model.vo.UrlVO
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonInclude
import cn.tsvico.system.model.Dept
import cn.tsvico.common.validate.groups.Create
import java.io.Serializable
import java.util.*

class Menu : Serializable {
    @JsonProperty("id")
    var menuId: Int? = null
    var parentId: Int? = null

    @JsonProperty("name")
    var menuName: String? = null
    var url: String? = null
    var perms: String? = null
    var orderNum: Int? = null

    @JsonIgnore
    var createTime: Date? = null

    @JsonIgnore
    var modifyTime: Date? = null

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var checkArr:String? = "0"

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var children: List<Menu>? = null
    var icon: String? = null

}