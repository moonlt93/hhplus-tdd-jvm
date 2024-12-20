package io.hhplus.tdd.point.service


interface UserQueryService<T> {

    fun getUserPointInfoById(id: Long): T

}
