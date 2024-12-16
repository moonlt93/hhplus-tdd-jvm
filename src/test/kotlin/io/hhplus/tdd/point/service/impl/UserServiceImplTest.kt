package io.hhplus.tdd.point.service.impl

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.UserPoint
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


class UserServiceImplTest: BehaviorSpec({
    given(" UserPointTable with data") {
        val userPointTable = UserPointTable()

        When("selectById is called for an existing ID") {
            val result = userPointTable.selectById(1L)

            Then("it should return the correct user point") {
                result.id shouldBe 1L
                result.point shouldBe 0
            }
        }
    }
}) {
    @Test
    @DisplayName("mock 객체로 userId 조회시 값이 어떻게 넘어오는지 테스트 ")
    fun `should return correct user point`() {

        val mockTable = mockk<UserPointTable>()

        every { mockTable.selectById(1L) } returns UserPoint(1L, 0,  System.currentTimeMillis())

        val result = mockTable.selectById(1L)

        result.id shouldBe 1L
        result.point shouldBe 0
    }

}
