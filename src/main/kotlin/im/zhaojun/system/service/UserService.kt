package im.zhaojun.system.service

import com.github.pagehelper.PageHelper
import im.zhaojun.common.exception.DuplicateNameException
import im.zhaojun.common.shiro.ShiroActionProperties
import im.zhaojun.common.util.TreeUtil
import im.zhaojun.system.mapper.UserMapper
import im.zhaojun.system.mapper.UserRoleMapper
import im.zhaojun.system.model.*
import im.zhaojun.system.service.UserService
import org.apache.shiro.authz.UnauthorizedException
import org.apache.shiro.crypto.hash.Md5Hash
import org.apache.shiro.session.mgt.eis.SessionDAO
import org.apache.shiro.subject.SimplePrincipalCollection
import org.apache.shiro.subject.support.DefaultSubjectContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService {
    @Autowired
    private val userMapper: UserMapper? = null

    @Autowired
    private val menuService: MenuService? = null

    @Autowired
    private val userRoleMapper: UserRoleMapper? = null

    @Autowired
    private val userAuthsService: UserAuthsService? = null

    @Autowired
    private val sessionDAO: SessionDAO? = null

    @Autowired
    private val shiroActionProperties: ShiroActionProperties? = null
    fun selectAllWithDept(page: Int, rows: Int, userQuery: User?): List<User>? {
        PageHelper.startPage<Any>(page, rows)
        return userMapper!!.selectAllWithDept(userQuery)
    }

    fun selectRoleIdsById(userId: Int?): Array<Int?>? {
        return userMapper!!.selectRoleIdsByUserId(userId)
    }

    @Transactional
    fun add(user: User, roleIds: Array<Int>): Int? {
        checkUserNameExistOnCreate(user.username)
        val salt = generateSalt()
        val encryptPassword = Md5Hash(user.password, salt).toString()
        user.salt = salt
        user.password = encryptPassword
        userMapper!!.insert(user)
        grantRole(user.userId, roleIds)
        return user.userId
    }

    fun updateLastLoginTimeByUsername(username: String?) {
        userMapper!!.updateLastLoginTimeByUsername(username)
    }

    fun disableUserByID(id: Int?): Boolean {
//        offlineByUserId(id); // 加上这段代码, 禁用用户后, 会将当前在线的用户立即踢出.
        return userMapper!!.updateStatusByPrimaryKey(id, 0) == 1
    }

    fun enableUserByID(id: Int?): Boolean {
        return userMapper!!.updateStatusByPrimaryKey(id, 1) == 1
    }

    /**
     * 根据用户 ID 激活账号.
     * @param userId    用户 ID
     */
    fun activeUserByUserId(userId: Int?) {
        userMapper!!.activeUserByUserId(userId)
    }

    @Transactional
    fun update(user: User, roleIds: Array<Int>): Boolean {
        checkUserNameExistOnUpdate(user)
        grantRole(user.userId, roleIds)
        return userMapper!!.updateByPrimaryKeySelective(user) == 1
    }

    fun selectOne(id: Int?): User? {
        return userMapper!!.selectByPrimaryKey(id)
    }

    /**
     * 新增时校验用户名是否重复
     * @param username  用户名
     */
    fun checkUserNameExistOnCreate(username: String?) {
        if (userMapper!!.countByUserName(username) > 0) {
            throw DuplicateNameException()
        }
    }

    fun checkUserNameExistOnUpdate(user: User) {
        if (userMapper!!.countByUserNameNotIncludeUserId(user.username, user.userId) > 0) {
            throw DuplicateNameException()
        }
    }

    fun offlineBySessionId(sessionId: String) {
        val session = sessionDAO!!.readSession(sessionId)
        if (session != null) {
            log.debug("成功踢出 sessionId 为 :" + sessionId + "的用户.")
            session.stop()
            sessionDAO.delete(session)
        }
    }

    /**
     * 删除所有此用户的在线用户
     */
    fun offlineByUserId(userId: Int) {
        val activeSessions = sessionDAO!!.activeSessions
        for (session in activeSessions) {
            val simplePrincipalCollection =
                session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) as SimplePrincipalCollection?
            if (simplePrincipalCollection != null) {
                val user = simplePrincipalCollection.primaryPrincipal as User?
                if (user != null && userId == user.userId) {
                    offlineBySessionId(session.id.toString())
                }
            }
        }
    }

    @Transactional
    fun grantRole(userId: Int?, roleIds: Array<Int>) {
        require(roleIds.isNotEmpty()) { "赋予的角色数组不能为空." }
        // 清空原有的角色, 赋予新角色.
        userRoleMapper!!.deleteUserRoleByUserId(userId)
        userRoleMapper.insertList(userId, roleIds)
    }

    fun selectByActiveCode(activeCode: String?): User? {
        return userMapper!!.selectByActiveCode(activeCode)
    }

    fun count(): Int {
        return userMapper!!.count()
    }

    @Transactional
    fun delete(userId: Int?) {
        // 检查删除的是否是超级管理员, 如果是, 则不允许删除.
        val user = userMapper!!.selectByPrimaryKey(userId)
        if (shiroActionProperties!!.superAdminUsername == user!!.username) {
            throw UnauthorizedException("试图删除超级管理员, 被禁止.")
        }
        userAuthsService!!.deleteByUserId(userId)
        userMapper.deleteByPrimaryKey(userId)
        userRoleMapper!!.deleteUserRoleByUserId(userId)
    }

    /**
     * 获取用户拥有的所有菜单权限和操作权限.
     * @param username      用户名
     * @return              权限标识符号列表
     */
    fun selectPermsByUsername(username: String?): Set<String?> {
        val perms: MutableSet<String> = HashSet()
        val menuTreeVOS: List<Menu> = menuService!!.selectMenuTreeVOByUsername(username)
        val leafNodeMenuList: List<Menu> = TreeUtil.getAllLeafNode(menuTreeVOS)
        for (menu in leafNodeMenuList) {
            if (menu.perms != null) {
                perms.add(menu.perms!!)
            }
        }
        perms.addAll(userMapper!!.selectOperatorPermsByUserName(username))
        return perms
    }

    fun selectRoleNameByUserName(username: String?): Set<String?>? {
        return userMapper!!.selectRoleNameByUserName(username)
    }

    fun selectOneByUserName(username: String?): User? {
        return userMapper!!.selectOneByUserName(username)
    }

    fun updatePasswordByUserId(userId: Int?, password: String?) {
        val salt = generateSalt()
        val encryptPassword = Md5Hash(password, salt).toString()
        userMapper!!.updatePasswordByUserId(userId, encryptPassword, salt)
    }

    private fun generateSalt(): String {
        return System.currentTimeMillis().toString()
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserService::class.java)
    }
}