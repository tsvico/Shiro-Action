package cn.tsvico.system.mapper

import cn.tsvico.system.model.SysLog
import org.apache.ibatis.annotations.Mapper

@Mapper
interface SysLogMapper {
    fun deleteByPrimaryKey(id: Int?): Int
    fun insert(sysLog: SysLog?): Int
    fun selectByPrimaryKey(id: Int?): SysLog?
    fun updateByPrimaryKey(sysLog: SysLog?): Int
    fun selectAll(): List<SysLog>?
    fun count(): Int
}