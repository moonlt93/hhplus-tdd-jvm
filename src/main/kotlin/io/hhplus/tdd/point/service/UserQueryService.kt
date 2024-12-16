package io.hhplus.tdd.point.service


interface UserQueryService<T> {

    fun getUserInfoById(id: Long): T

}
