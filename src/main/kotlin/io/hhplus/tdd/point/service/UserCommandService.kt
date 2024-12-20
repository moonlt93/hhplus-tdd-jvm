package io.hhplus.tdd.point.service

interface UserCommandService<T> {

    fun chargeUserPointById(id: Long, amount: Long): T

    fun useUserPointById(id: Long, amount: Long): T

}
