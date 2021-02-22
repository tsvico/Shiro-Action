package cn.tsvico.system.service

import cn.tsvico.system.mapper.DeptMapper
import cn.tsvico.system.model.Dept
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeptService {
    @Autowired
    private val deptMapper: DeptMapper? = null
    fun insert(dept: Dept): Dept {
        val maxOrderNum = deptMapper!!.selectMaxOrderNum()
        dept.orderNum = maxOrderNum + 1
        deptMapper.insert(dept)
        return dept
    }

    fun deleteByPrimaryKey(deptId: Int?): Int {
        return deptMapper!!.deleteByPrimaryKey(deptId)
    }

    fun updateByPrimaryKey(dept: Dept?): Dept? {
        deptMapper!!.updateByPrimaryKey(dept)
        return dept
    }

    fun selectByPrimaryKey(deptId: Int?): Dept? {
        return deptMapper!!.selectByPrimaryKey(deptId)
    }

    /**
     * 删除当前部门及子部门.
     */
    fun deleteCascadeByID(deptId: Int?) {
        val childIDList = deptMapper!!.selectChildrenIDByPrimaryKey(deptId)
        for (childId in childIDList!!) {
            deleteCascadeByID(childId)
        }
        deleteByPrimaryKey(deptId)
    }

    /**
     * 根据父 ID 查询部门
     */
    fun selectByParentId(parentId: Int?): List<Dept?>? {
        return deptMapper!!.selectByParentId(parentId)
    }

    /**
     * 查找所有的部门的树形结构
     */
    fun selectAllDeptTree(): List<Dept>? {
        return deptMapper!!.selectAllTree()
    }

    /**
     * 获取所有菜单并添加一个根节点 (树形结构)
     */
    fun selectAllDeptTreeAndRoot(): List<Dept> {
        val deptList = selectAllDeptTree()
        val root = Dept()
        root.deptId = 0
        root.deptName = "根部门"
        root.children = deptList
        val rootList: MutableList<Dept> = ArrayList()
        rootList.add(root)
        return rootList
    }

    fun swapSort(currentId: Int?, swapId: Int?) {
        deptMapper!!.swapSort(currentId, swapId)
    }
}