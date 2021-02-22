package cn.tsvico.system.mapper

import cn.tsvico.system.model.*
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface UserMapper {
    fun deleteByPrimaryKey(userId: Int?): Int
    fun insert(user: User?): Int
    fun selectByPrimaryKey(userId: Int?): User?
    fun updateByPrimaryKeySelective(user: User?): Int
    fun updateByPrimaryKey(user: User?): Int

    /**
     * 获取用户所拥有的所有权限
     */
    fun selectPermsByUserName(@Param("username") username: String?): Set<String?>?

    /**
     * 获取用户所拥有的所有角色
     */
    fun selectRoleNameByUserName(@Param("username") username: String?): Set<String?>?

    /**
     * 根据用户名获取用户
     */
    fun selectOneByUserName(@Param("username") username: String?): User?

    /**
     * 获取所有用户
     */
    fun selectAll(): List<User?>?

    /**
     * 获取所有用户
     */
    fun selectAllWithDept(userQuery: User?): List<User>?

    /**
     * 更改用户的状态为某项值
     */
    fun updateStatusByPrimaryKey(@Param("id") id: Int?, @Param("status") status: Int): Int

    /**
     * 更新用户最后登录事件
     */
    fun updateLastLoginTimeByUsername(@Param("username") username: String?): Int

    /**
     * 统计已经有几个此用户名, 用来检测用户名是否重复.
     */
    fun countByUserName(@Param("username") username: String?): Int

    /**
     * 统计已经有几个此用户名, 用来检测用户名是否重复 (不包含某用户 ID).
     */
    fun countByUserNameNotIncludeUserId(@Param("username") username: String?, @Param("userId") userId: Int?): Int

    /**
     * 查询此用户拥有的所有角色的 ID
     *
     * @param userId 用户 ID
     * @return 拥有的角色 ID 数组
     */
    fun selectRoleIdsByUserId(@Param("userId") userId: Int?): Array<Int?>?

    /**
     * 根据邮箱激活码, 查询要被激活的用户.
     */
    fun selectByActiveCode(@Param("activeCode") activeCode: String?): User?

    /**
     * 统计系统中有多少个用户.
     */
    fun count(): Int

    /**
     * 获取用户所拥有的操作权限
     */
    fun selectOperatorPermsByUserName(@Param("username") username: String?): Set<String>
    fun updatePasswordByUserId(
        @Param("userId") userId: Int?,
        @Param("password") password: String?,
        @Param("salt") salt: String?
    ): Int

    fun activeUserByUserId(userId: Int?): Int //    selectAllByUsernameLikeAndStatus
}