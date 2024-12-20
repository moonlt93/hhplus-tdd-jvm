package io.hhplus.tdd.point

import io.hhplus.tdd.point.service.GetUserHistoryUseCase
import io.hhplus.tdd.point.service.UserCommandService
import io.hhplus.tdd.point.service.UserQueryService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch

@WebMvcTest(PointController::class)
class PointControllerTest(
    @Autowired val mockMvc: MockMvc
) {

    @MockBean
    lateinit var userQueryService: UserQueryService<UserPoint>

    @MockBean
    lateinit var userCommandService: UserCommandService<UserPoint>

    @MockBean
    lateinit var getUserHistoryUseCase: GetUserHistoryUseCase<PointHistory>

    @Test
    @DisplayName("특정 유저의 포인트 조회")
    fun findUserPointById() {
        val userId = 1L
        val userPoint = UserPoint(id = userId, point = 200L, System.currentTimeMillis())

        given(userQueryService.getUserPointInfoById(userId)).willReturn(userPoint)

        mockMvc.get("/point/$userId")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.id") { value(userId) }
                jsonPath("$.point") { value(200) }
            }
    }

    @Test
    @DisplayName("특정 유저의 포인트 히스토리 목록 조회")
    fun findPointHistoryListByUserId() {
        val userId = 1L
        val histories = listOf(
            PointHistory(1L, 1L, TransactionType.CHARGE, 200L, 1234567890),
            PointHistory(2L, 1L, TransactionType.USE, 100L, 1234567890)
        )

        given(getUserHistoryUseCase.getUserPointHistory(userId)).willReturn(histories)

        mockMvc.get("/point/$userId/histories")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0].userId") { value(userId) }
                jsonPath("$[0].amount") { value(200) }
                jsonPath("$[0].type") { value(TransactionType.CHARGE.name) }

                jsonPath("$[1].userId") { value(userId) }
                jsonPath("$[1].amount") { value(100) }
                jsonPath("$[1].type") { value(TransactionType.USE.name) }
            }
    }

    @Test
    @DisplayName("특정 유저 포인트 충전")
    fun chargePointByUserId() {
        val userId = 1L
        val chargeAmount = 100L
        val userPoint = UserPoint(id = userId, point = 300L, System.currentTimeMillis())

        given(userCommandService.chargeUserPointById(userId, chargeAmount)).willReturn(userPoint)

        mockMvc.patch("/point/$userId/charge") {
            param("amount", chargeAmount.toString())
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(userId) }
            jsonPath("$.point") { value(300) }
        }
    }

    @Test
    @DisplayName("특정 유저 포인트 사용")
    fun useUserPointByUserId() {
        val userId = 1L
        val useAmount = 50L
        val userPoint = UserPoint(id = userId, point = 150L, System.currentTimeMillis())

        given(userCommandService.useUserPointById(userId, useAmount)).willReturn(userPoint)

        mockMvc.patch("/point/$userId/use") {
            param("amount", useAmount.toString())
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(userId) }
            jsonPath("$.point") { value(150) }
        }
    }
}