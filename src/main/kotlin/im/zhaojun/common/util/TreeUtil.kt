package im.zhaojun.common.util

import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils
import java.lang.reflect.Field
import java.util.*
import javax.validation.constraints.NotNull
import im.zhaojun.common.exception.TreeCastException

import java.util.ArrayList




object TreeUtil {
    /**
     * 集合转树结构
     *
     * @param list       目标集合
     * @param id         节点编号字段名称
     * @param parent     父节点编号字段名称
     * @param children   子节点集合属性名称
     * @param clazz      集合元素类型
     * @return           转换后的树形结构
     */
    fun <T> toTree(
        list: MutableList<T>,
        id: String?,
        parent: String?,
        children: String?,
        clazz: Class<T>
    ): List<T> {
        var tmpId = id
        var tmpParent = parent
        var tmpChildren = children
        return try {
            // 如果目标集合为空,直接返回一个空树
            if (list.isEmpty()) {
                return emptyList()
            }
            // 如果被依赖字段名称为空则默认为 id
            if (StringUtils.isEmpty(tmpId)) {
                tmpId = "id"
            }
            // 如果依赖字段为空则默认为parent
            if (StringUtils.isEmpty(tmpParent)) {
                tmpParent = "parent"
            }
            // 如果子节点集合属性名称为空则默认为children
            if (StringUtils.isEmpty(tmpChildren)) {
                tmpChildren = "children"
            }

            // 初始化根节点集合, 支持 Set 和 List
            val roots: MutableList<T> = ArrayList()

            // 获取 id 字段, 从当前对象或其父类
            val idField: Field = try {
                clazz.getDeclaredField(tmpId!!)
            } catch (e1: NoSuchFieldException) {
                clazz.superclass.getDeclaredField(tmpId!!)
            }

            // 获取 parentId 字段, 从当前对象或其父类
            val parentField: Field = try {
                clazz.getDeclaredField(tmpParent!!)
            } catch (e1: NoSuchFieldException) {
                clazz.superclass.getDeclaredField(tmpParent!!)
            }

            // 获取 children 字段, 从当前对象或其父类
            val childrenField: Field = try {
                clazz.getDeclaredField(tmpChildren!!)
            } catch (e1: NoSuchFieldException) {
                clazz.superclass.getDeclaredField(tmpChildren!!)
            }

            // 设置为可访问
            idField.isAccessible = true
            parentField.isAccessible = true
            childrenField.isAccessible = true

            // 找出所有的根节点
            for (c in list) {
                val parentId = parentField[c]
                if (isRootNode(parentId)) {
                    roots.add(c)
                }
            }

            // 从目标集合移除所有根节点
            list.removeAll(roots)

            // 遍历根节点, 依次添加子节点
            for (root in roots) {
                addChild(root, list, idField, parentField, childrenField)
            }

            // 关闭可访问
            idField.isAccessible = false
            parentField.isAccessible = false
            childrenField.isAccessible = false
            roots
        } catch (e: Exception) {
            e.printStackTrace()
            throw TreeCastException(e)
        }
    }

    /**
     * 为目标节点添加孩子节点
     *
     * @param node          目标节点
     * @param list          目标集合
     * @param idField       ID 字段
     * @param parentField   父节点字段
     * @param childrenField 字节点字段
     */
    @Throws(IllegalAccessException::class)
    private fun <T> addChild(
        node: @NotNull T?,
        list: @NotNull MutableList<T>?,
        idField: @NotNull Field?,
        parentField: @NotNull Field?,
        childrenField: @NotNull Field?
    ) {
        val id = idField!![node]
        var children = childrenField!![node] as MutableList<T>?
        // 如果子节点的集合为 null, 初始化孩子集合
        if (children == null) {
            children = ArrayList()
        }
        for (t in list!!) {
            val o = parentField!![t]
            if (id == o) {
                // 将当前节点添加到目标节点的孩子节点
                children.add(t)
                // 重设目标节点的孩子节点集合,这里必须重设,因为如果目标节点的孩子节点是null的话,这样是没有地址的,就会造成数据丢失,所以必须重设,如果目标节点所在类的孩子节点初始化为一个空集合,而不是null,则可以不需要这一步,因为java一切皆指针
                childrenField[node] = children
                // 递归添加孩子节点
                addChild(t, list, idField, parentField, childrenField)
            }
        }
    }

    /**
     * 判断是否是根节点, 判断方式为: 父节点编号为空或为 0, 则认为是根节点. 此处的判断应根据自己的业务数据而定.
     * @param parentId      父节点编号
     * @return              是否是根节点
     */
    private fun isRootNode(parentId: Any?): Boolean {
        var flag = false
        if (parentId == null) {
            flag = true
        } else if (parentId is String && (StringUtils.isEmpty(parentId) || "0" == parentId)) {
            flag = true
        } else if (parentId is Int && Integer.valueOf(0) == parentId) {
            flag = true
        }
        return flag
    }

    /**
     * 获取树形结构中的所有叶子节点 (前提是已经是树形结构), 默认子节点字段为: "children"
     */
    fun <T> getAllLeafNode(list: List<T>?): MutableList<T> {
        if(list?.isEmpty() == true){
            return mutableListOf()
        }
        return getAllLeafNode(list!!, null)
    }

    /**
     * 获取树形结构中的所有叶子节点 (前提是已经是树形结构)
     * @param list      树形结构
     * @param children  child 字段名称
     */
    private fun <T> getAllLeafNode(list: List<T>, children: String?): MutableList<T> {
        var children = children
        return try {
            if (StringUtils.isEmpty(children)) children = "children"
            val result: MutableList<T> = ArrayList()
            val queue: Queue<T?> = ArrayDeque()
            for (item in list) {
                val childrenField: Field = (item as Any).javaClass.getDeclaredField(children)
                childrenField.isAccessible = true
                val childrenList = childrenField[item] as List<T?>?
                if (CollectionUtils.isEmpty(childrenList)) {
                    result.add(item)
                } else {
                    queue.addAll(childrenList!!)
                }
            }
            while (!queue.isEmpty()) {
                val item = queue.poll()
                val childrenField: Field = (item as Any).javaClass.getDeclaredField(children)
                childrenField.isAccessible = true
                val childrenList = childrenField[item] as List<T?>?
                if (CollectionUtils.isEmpty(childrenList)) {
                    result.add(item)
                } else {
                    childrenList?.let { queue.addAll(it) }
                }
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            throw TreeCastException(e)
        }
    }
}