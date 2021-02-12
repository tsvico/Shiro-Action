package im.zhaojun.system.service

import com.github.pagehelper.PageHelper
import im.zhaojun.system.mapper.SysLogMapper
import im.zhaojun.system.model.SysLog
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SysLogService {
    @Autowired
    private val sysLogMapper: SysLogMapper? = null
    fun selectAll(page: Int, rows: Int): List<SysLog>? {
        PageHelper.startPage<Any>(page, rows)
        return sysLogMapper!!.selectAll()
    }

    fun count(): Int {
        return sysLogMapper!!.count()
    }
}