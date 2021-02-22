package cn.tsvico.system.service

import cn.tsvico.system.mapper.RoleOperatorMapper
import cn.tsvico.system.model.RoleOperator
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