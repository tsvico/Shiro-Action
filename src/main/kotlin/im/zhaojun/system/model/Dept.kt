package im.zhaojun.system.model

import com.fasterxml.jackson.annotation.JsonIgnore
import im.zhaojun.system.model.vo.UrlVO
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonInclude
import im.zhaojun.system.model.Dept
import im.zhaojun.common.validate.groups.Create
import java.io.Serializable
import java.util.*

class Dept : Serializable {
    /**
     * 部门ID
     */
    @JsonProperty("id")
    var deptId: Int? = null

    /**
     * 部门名称
     */
    @JsonProperty("name")
    var deptName: String? = null

    /**
     * 上级部门 ID
     */
    var parentId: Int? = null

    /**
     * 排序
     */
    var orderNum: Int? = null

    /**
     * 创建时间
     */
    @JsonIgnore
    var createTime: Date? = null

    /**
     * 修改时间
     */
    @JsonIgnore
    var modifyTime: Date? = null

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var children: List<Dept>? = null
    override fun toString(): String {
        return "Dept{" +
                "deptId=" + deptId +
                ", deptName='" + deptName + '\'' +
                ", parentId=" + parentId +
                ", orderNum=" + orderNum +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", children=" + children +
                '}'
    }
}