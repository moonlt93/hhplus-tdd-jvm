package io.hhplus.tdd.point.service.impl

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.TransactionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class UserHistoryServiceImplTest {

    private lateinit var pointHistoryTable: PointHistoryTable

    @BeforeEach
    fun setUp() {

        pointHistoryTable = PointHistoryTable()
        pointHistoryTable.insert(1, 100, TransactionType.CHARGE, System.currentTimeMillis())

    }

    @Test
    @DisplayName(" point history 조회시 원하는 size 만큼 가져오는지 test ")
    fun `should return correct pointHistory size test`() {
        // Given
        val userId = 1L

        // When
        val pointHistory = pointHistoryTable.selectAllByUserId(userId)

        // Then
        assertThat(pointHistory).size().isEqualTo(1) // 정확히 0과 100이 반환되는지 검증

    }


    @Test
    @DisplayName("history에 들어간 userId가 없을때 ")
    fun `should return empty list if no history exists`() {
        // Given
        val nonExistingUserId = 999L

        // When
        val pointHistory = pointHistoryTable.selectAllByUserId(nonExistingUserId)

        // Then
        assertThat(pointHistory).isEmpty()
    }


}