package io.hhplus.tdd.point.service

interface GetUserHistoryUseCase<PointHistory> {

    fun getUserPointHistory(id: Long): List<PointHistory>
}