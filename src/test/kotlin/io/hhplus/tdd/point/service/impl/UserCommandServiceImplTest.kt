package io.hhplus.tdd.point.service.impl

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.TransactionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

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


}

