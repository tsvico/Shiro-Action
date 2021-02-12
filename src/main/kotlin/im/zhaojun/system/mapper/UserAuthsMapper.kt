package im.zhaojun.system.mapper

import im.zhaojun.system.model.Dept
import im.zhaojun.system.model.SysLog
import im.zhaojun.system.model.LoginLog
import im.zhaojun.system.model.RoleMenu
import im.zhaojun.system.model.UserRole
import im.zhaojun.system.model.UserAuths
import im.zhaojun.system.model.RoleOperator
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface UserAuthsMapper {
    fun deleteByPrimaryKey(id: Int?): Int
    fun insert(record: UserAuths?): Int
    fun selectByPrimaryKey(id: Int?): UserAuths?
    fun updateByPrimaryKey(record: UserAuths?): Int
    fun selectOneByIdentityTypeAndUserId(
        @Param("identityType") identityType: String?,
        @Param("userId") userId: Int?
    ): UserAuths?

    fun selectOneByIdentifier(@Param("identifier") identifier: String?): List<UserAuths?>?
    fun deleteByUserId(@Param("userId") userId: Int?): Int
    fun selectOneByIdentityTypeAndIdentifier(
        @Param("identityType") identityType: String?,
        @Param("identifier") identifier: String?
    ): UserAuths?
}