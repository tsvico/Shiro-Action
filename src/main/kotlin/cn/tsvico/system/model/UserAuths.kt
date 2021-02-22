package cn.tsvico.system.model

/**
 * @author Zhao Jun
 * 2019/5/26 15:49
 */
class UserAuths {
    /**
     * 主键
     */
    var id: Int? = null

    /**
     * 用户 ID
     */
    var userId: Int? = null

    /**
     * 登录类型
     */
    var identityType: String? = null

    /**
     * 第三方登录的用户名
     */
    var identifier: String? = null

    /**
     * 第三方登录 token
     */
    var credential: String? = null
}