package com.valentinvstoyanov.rekord.users.data

import com.valentinvstoyanov.rekord.users.data.dao.UserDao
import com.valentinvstoyanov.rekord.users.data.entity.DbUser
import com.valentinvstoyanov.rekord.users.domain.UserRepository
import com.valentinvstoyanov.rekord.users.domain.model.CreateUser
import com.valentinvstoyanov.rekord.users.domain.model.User
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class PersistentUserRepository(private val userDao: UserDao) : UserRepository {
    override fun create(user: CreateUser): Mono<User> = userDao.insert(DbUser.from(user)).map { it.toUser() }

    override fun update(user: User): Mono<User> = userDao.save(DbUser.from(user)).map { it.toUser() }

    override fun delete(id: String): Mono<User> =
        userDao.findById(id).flatMap { userDao.delete(it).thenReturn(it) }.map { it.toUser() }

    override fun getByEmail(email: String): Mono<User> = userDao.getByEmail(email).map { it.toUser() }

    override fun getByUsername(username: String): Mono<User> = userDao.getByUsername(username).map { it.toUser() }

    override fun getById(id: String): Mono<User> = userDao.findById(id).map { it.toUser() }

    override fun getAll(): Flux<User> = userDao.findAll().map { it.toUser() }
}