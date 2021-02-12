package im.zhaojun.system.model

import com.fasterxml.jackson.annotation.JsonIgnore
import im.zhaojun.system.model.vo.UrlVO
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonInclude
import im.zhaojun.system.model.Dept
import im.zhaojun.common.validate.groups.Create
import java.io.Serializable
import java.util.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class User : Serializable {
    var userId: Int? = null
    var username: @NotBlank(message = "用户名不能为空") String? = null

    @JsonIgnore
    var password: @NotBlank(message = "密码不能为空", groups = [Create::class]) String? = null

    @JsonIgnore
    var salt: String? = null
    var email: @Email(message = "邮箱格式不正确") String? = null
    var status: String? = null
    var lastLoginTime: Date? = null
    var createTime: Date? = null

    @JsonIgnore
    var modifyTime: Date? = null

    @JsonIgnore
    var activeCode: String? = null
    var deptId: Int? = null
    var deptName: String? = null
    override fun toString(): String {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", activeCode='" + activeCode + '\'' +
                ", deptId=" + deptId +
                ", deptName='" + deptName + '\'' +
                '}'
    }
}