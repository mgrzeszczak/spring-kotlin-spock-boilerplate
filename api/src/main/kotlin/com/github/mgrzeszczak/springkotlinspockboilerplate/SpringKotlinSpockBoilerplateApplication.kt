package com.github.mgrzeszczak.springkotlinspockboilerplate

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import javax.annotation.PostConstruct
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@SpringBootApplication
class SpringKotlinSpockBoilerplateApplication

@Entity
data class User(@Id @GeneratedValue var id: Long? = null, var username: String? = null)

@Repository
interface UserRepository : JpaRepository<User, Long> {

}

fun UserRepository.rxSave(user: User): Mono<User> {
    return Mono.fromCallable { save(user) }
            .subscribeOn(Schedulers.elastic())
}

fun UserRepository.rxFind(id: Long): Mono<User> {
    return Mono.fromCallable { findById(id).orElse(null) }
            .subscribeOn(Schedulers.elastic())
}

@RestController
@RequestMapping("/api/users")
class UserController(val userRepository: UserRepository) {

    companion object {
        val logger = LoggerFactory.getLogger(UserController::class.java)
    }

    @PostConstruct
    fun preload() {
        userRepository.rxSave(User(username = "user"))
                .subscribe({ logger.info("{}", it) })
    }

    @GetMapping("/{id}")
    fun get(@PathVariable("id") id: Long): Mono<User> {
        return userRepository.rxFind(id)
    }

}

fun main(args: Array<String>) {
    runApplication<SpringKotlinSpockBoilerplateApplication>(*args)
}
