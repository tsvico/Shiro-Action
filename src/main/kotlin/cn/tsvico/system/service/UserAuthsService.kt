package cn.tsvico.system.service

import cn.tsvico.common.constants.AuthcTypeEnum
import cn.tsvico.system.mapper.UserAuthsMapper
import cn.tsvico.system.model.UserAuths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserAuthsService {
    @Autowired
    private lateinit var userAuthsMapper: UserAuthsMapper
    fun deleteByPrimaryKey(id: Int?): Int {
        return userAuthsMapper.deleteByPrimaryKey(id)
    }

    fun insert(record: UserAuths?): Int {
        return userAuthsMapper.insert(record)
    }

    fun selectByPrimaryKey(id: Int?): UserAuths? {
        return userAuthsMapper.selectByPrimaryKey(id)
    }

    fun updateByPrimaryKey(record: UserAuths?): Int {
        return userAuthsMapper.updateByPrimaryKey(record)
    }

    fun selectOneByIdentityTypeAndUserId(authcTypeEnum: AuthcTypeEnum, userId: Int?): UserAuths? {
        return userAuthsMapper.selectOneByIdentityTypeAndUserId(authcTypeEnum.description, userId)
    }

    fun selectOneByIdentityTypeAndIdentifier(authcTypeEnum: AuthcTypeEnum, identifier: String?): UserAuths? {
        return userAuthsMapper.selectOneByIdentityTypeAndIdentifier(authcTypeEnum.description, identifier)
    }

    fun deleteByUserId(userId: Int?): Int {
        return userAuthsMapper.deleteByUserId(userId)
    }
}