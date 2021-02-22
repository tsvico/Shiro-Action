package cn.tsvico.system.mapper

import cn.tsvico.system.model.Dept
import cn.tsvico.system.model.SysLog
import cn.tsvico.system.model.LoginLog
import cn.tsvico.system.model.RoleMenu
import cn.tsvico.system.model.UserRole
import cn.tsvico.system.model.UserAuths
import cn.tsvico.system.model.RoleOperator
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