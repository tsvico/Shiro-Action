package cn.tsvico.system.mapper

import cn.tsvico.system.model.Dept
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface DeptMapper {
    fun deleteByPrimaryKey(deptId: Int?): Int
    fun insert(dept: Dept?): Int
    fun selectByPrimaryKey(deptId: Int?): Dept?
    fun updateByPrimaryKey(dept: Dept?): Int
    fun selectByParentId(@Param("parentId") parentId: Int?): List<Dept?>?
    fun selectAllTree(): List<Dept>?
    fun selectChildrenIDByPrimaryKey(@Param("deptId") deptId: Int?): List<Int?>?
    fun selectMaxOrderNum(): Int
    fun swapSort(@Param("currentId") currentId: Int?, @Param("swapId") swapId: Int?): Int
}