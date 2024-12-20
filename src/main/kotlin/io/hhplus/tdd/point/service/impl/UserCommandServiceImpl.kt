package io.hhplus.tdd.point.service.impl

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.TransactionType
import io.hhplus.tdd.point.UserPoint
import io.hhplus.tdd.point.service.UserCommandService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
class UserCommandServiceImpl(
    private val pointTable: UserPointTable,
    private val pointHistoryTable: PointHistoryTable
) : UserCommandService<UserPoint> {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val lock = ReentrantLock()

    override fun chargeUserPointById(id: Long, amount: Long): UserPoint {

        return lock.withLock {
            chargeUserPoint(id, amount)
        }

    }

    override fun useUserPointById(id: Long, amount: Long): UserPoint {

        return lock.withLock {
            useUserPoint(id, amount)
        }

    }

    private fun useUserPoint(id: Long, amount: Long): UserPoint {
        val selectById = pointTable.selectById(id)

        if (selectById.point < amount) {

            throw IllegalArgumentException("사용하려는 point amount가 id로 조회한 포인트보다 많습니다.")

        }

        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis())

        selectById.point -= amount

        logger.info("회원 id {} 포인트가 차감되었습니다. : {}" + "남은 포인트는  {} 입니다.", id, amount, selectById.point)

        return pointTable.insertOrUpdate(selectById.id, selectById.point)
    }

    private fun chargeUserPoint(
        id: Long,
        amount: Long,
    ): UserPoint {

        if (amount <= 0) {
            throw IllegalArgumentException("요청한 amount가 0이하 입니다.")
        }

        print(logger.name + "chargeUserPointById attach")

        val insertPointHistory =
            pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis())
        val userPoint = pointTable.insertOrUpdate(insertPointHistory.id, insertPointHistory.amount)

        return userPoint
    }


}