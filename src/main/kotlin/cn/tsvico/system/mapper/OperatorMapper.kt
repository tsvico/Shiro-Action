package cn.tsvico.system.mapper

import cn.tsvico.system.model.*
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface OperatorMapper {
    fun deleteByPrimaryKey(operatorId: Int?): Int
    fun insert(operator: Operator?): Int
    fun selectByPrimaryKey(operatorId: Int?): Operator?
    fun updateByPrimaryKey(operator: Operator?): Int
    fun selectByMenuId(@Param("menuId") menuId: Int?): List<Operator?>?
    fun selectAll(): List<Operator>?
    fun deleteByMenuId(menuId: Int?): Int
}