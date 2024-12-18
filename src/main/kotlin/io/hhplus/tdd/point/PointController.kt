package io.hhplus.tdd.point

import io.hhplus.tdd.point.service.GetUserHistoryUseCase
import io.hhplus.tdd.point.service.UserCommandService
import io.hhplus.tdd.point.service.UserQueryService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/point")
class PointController(
    private val userQueryService: UserQueryService<UserPoint>,
    private val userCommandService: UserCommandService<UserPoint>,
    private val getUserHistoryUseCase: GetUserHistoryUseCase<PointHistory>
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    fun point(
        @PathVariable id: Long,
    ): UserPoint {

        return userQueryService.getUserPointInfoById(id)

    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    fun history(
        @PathVariable id: Long,
    ): List<PointHistory> {

        return getUserHistoryUseCase.getUserPointHistory(id)

    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    fun charge(
        @PathVariable id: Long,
        @RequestParam amount: Long,
    ): UserPoint {
        return userCommandService.chargeUserPointById(id, amount)
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    fun use(
        @PathVariable id: Long,
        @RequestParam amount: Long,
    ): UserPoint {
        return userCommandService.useUserPointById(id, amount)
    }
}