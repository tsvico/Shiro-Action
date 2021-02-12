package im.zhaojun.system.mapper

import im.zhaojun.system.model.Role
import org.apache.ibatis.annotations.Mapper

@Mapper
interface RoleMapper {
    fun deleteByPrimaryKey(roleId: Int?): Int
    fun insert(role: Role?): Int
    fun selectByPrimaryKey(roleId: Int?): Role?
    fun updateByPrimaryKey(role: Role?): Int
    fun selectAll(): List<Role?>?
    fun selectAllByQuery(roleQuery: Role?): List<Role>?
    fun count(): Int
}