package cn.tsvico.system.mapper

import cn.tsvico.system.model.LoginLog
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface LoginLogMapper {
    fun deleteByPrimaryKey(id: Int?): Int
    fun insert(loginLog: LoginLog?): Int
    fun selectByPrimaryKey(id: Int?): LoginLog?
    fun updateByPrimaryKey(loginLog: LoginLog?): Int
    fun selectAll(): List<LoginLog>
    fun recentlyWeekLoginCount(@Param("username") username: String?): List<Int>
    fun count(): Int
}