package im.zhaojun.system.service

import im.zhaojun.system.mapper.RoleOperatorMapper
import im.zhaojun.system.model.RoleOperator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RoleOperatorService {
    @Autowired
    private lateinit var roleOperatorMapper: RoleOperatorMapper
    fun insert(roleOperator: RoleOperator?): Int {
        return roleOperatorMapper.insert(roleOperator)
    }
}