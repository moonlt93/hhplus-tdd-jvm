package io.hhplus.tdd.point.service.impl

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.service.GetUserHistoryUseCase
import org.springframework.stereotype.Service

@Service
class UserHistoryServiceImpl(
    private val pointHistoryTable: PointHistoryTable,
) : GetUserHistoryUseCase<PointHistory> {

    override fun getUserPointHistory(id: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(id)
    }

}