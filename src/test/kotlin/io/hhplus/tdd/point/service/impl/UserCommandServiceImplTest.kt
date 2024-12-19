package io.hhplus.tdd.point.service.impl

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.TransactionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class UserCommandServiceImplTest {


    private lateinit var pointHistoryTable: PointHistoryTable
    private lateinit var userPointTable: UserPointTable
    private lateinit var userCommandService: UserCommandServiceImpl


    @BeforeEach
    fun setUp() {

        pointHistoryTable = PointHistoryTable()
        userPointTable = UserPointTable()
        userCommandService = UserCommandServiceImpl(userPointTable, pointHistoryTable);

    }

    @Test
    @DisplayName(" 해당하는 유저 point 충전 값이 맞는 지 확인  ")
    fun `should charge userPoint well`() {
        // Given
        val userId = 1L

        // When
        val insertedPointHistory =
            pointHistoryTable.insert(userId, 100, TransactionType.CHARGE, System.currentTimeMillis())
        val updateUserPoint = userPointTable.insertOrUpdate(userId, 100)

        val pointHistoryList = pointHistoryTable.selectAllByUserId(userId);
        // Then
        assertThat(updateUserPoint.point).isEqualTo(100)
        assertThat(pointHistoryList).size().isEqualTo(1)

    }

    @Test
    @DisplayName(" 포인트 충전 메서드가 잘 동작하는지 검증 ")
    fun `chargeUserPointById do well`() {
        // Given
        val userId = 1L
        val chargeAmount = 200L
        // When
        userCommandService.chargeUserPointById(userId, chargeAmount)
        val userPoint = userPointTable.selectById(userId);
        // Then
        assertThat(userPoint.point).isEqualTo(chargeAmount)
        assertThat(userPoint.id).isEqualTo(1L)

    }


    @Test
    @DisplayName(" 포인트가 부족할 때 예외 발생 검증  ")
    fun `useUserPointById do well minus amount`() {
        // Given
        val userId = 1L
        val chargeAmount = 100L
        val excessiveAmount = 200L

        // 초기 포인트 충전
        userCommandService.chargeUserPointById(userId, chargeAmount)

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            userCommandService.useUserPointById(userId, excessiveAmount)
        }

        // 예외 메시지 검증
        assertThat(exception.message).isEqualTo("들어온 input amount가 id로 조회한 포인트보다 많습니다.")

        // 포인트가 변경되지 않았는지 확인
        val userPoint = userPointTable.selectById(userId)
        val selectAllByUserId = pointHistoryTable.selectAllByUserId(userId)
        assertThat(userPoint.point).isEqualTo(chargeAmount)
        assertThat(selectAllByUserId).size().isEqualTo(1)

    }

    @Test
    @DisplayName(" 동시성 이슈 test  포인트차감 시 0이 되는지 확인 ")
    fun `Concurrency Problem test`() {

        val threadCount = 10
        val iterationsPerThread = 100

        userCommandService.chargeUserPointById(1L, threadCount * iterationsPerThread * 10L)

        val latch = CountDownLatch(threadCount)
        val executor = Executors.newFixedThreadPool(threadCount)

        repeat(threadCount) {
            executor.execute {
                latch.countDown() // 스레드 준비 완료
                latch.await() // 다른 스레드와 동시에 시작
                repeat(iterationsPerThread) {
                    userCommandService.useUserPointById(1L, 10)
                }
            }
        }

        // 스레드가 모두 작업을 끝낼 때까지 대기
        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.MINUTES)

        // 예상 결과 확인
        val finalPoint = userPointTable.selectById(1L) // 포인트 조회 메서드
        assertEquals(0, finalPoint.point, "User point mismatch")

    }


}

