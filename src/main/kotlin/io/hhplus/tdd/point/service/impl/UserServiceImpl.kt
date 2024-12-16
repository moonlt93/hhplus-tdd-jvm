package io.hhplus.tdd.point.service.impl

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.UserPoint
import io.hhplus.tdd.point.service.UserQueryService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val pointTable: UserPointTable) : UserQueryService<UserPoint> {

    override fun getUserInfoById(id: Long): UserPoint {
        return pointTable.selectById(id);
    }


}