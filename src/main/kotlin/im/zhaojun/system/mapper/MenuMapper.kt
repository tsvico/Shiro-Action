package im.zhaojun.system.mapper

import im.zhaojun.system.model.*
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface MenuMapper {
    fun deleteByPrimaryKey(menuId: Int?): Int
    fun insert(menu: Menu?): Int
    fun selectByPrimaryKey(menuId: Int?): Menu?
    fun updateByPrimaryKey(menu: Menu?): Int

    /**
     * 获取所有菜单
     */
    fun selectAll(): List<Menu>
    fun selectAllTree(): List<Menu>?
    fun selectAllMenuAndCountOperator(): List<Menu>?
    fun selectByParentId(parentId: Int?): List<Menu?>?

    /**
     * 删除当前菜单的所有子菜单
     */
    fun deleteByParentId(parentId: Int?): Int

    /**
     * 查找某菜单的所有子类 ID
     */
    fun selectChildrenIDByPrimaryKey(@Param("menuId") menuId: Int?): List<Int?>?

    /**
     * 获取某个用户的所拥有的导航菜单
     */
    fun selectMenuByUserName(@Param("userName") userName: String?): List<Menu>
    fun count(): Int

    /**
     * 交换两个菜单的顺序
     */
    fun swapSort(@Param("currentId") currentId: Int?, @Param("swapId") swapId: Int?): Int
    fun selectMaxOrderNum(): Int
}