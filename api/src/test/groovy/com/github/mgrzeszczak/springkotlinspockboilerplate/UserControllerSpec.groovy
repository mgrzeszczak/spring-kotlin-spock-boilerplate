package com.github.mgrzeszczak.springkotlinspockboilerplate

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification
import spock.mock.DetachedMockFactory

@WebFluxTest(UserController.class)
@Import(TestConfig.class)
class UserControllerSpec extends Specification {

    @Autowired
    UserRepository userRepository

    @Autowired
    WebTestClient client

    def "should find user"() {
        given:
            userRepository.findById(1L) >> Optional.of(new User(1L, "user"))
        expect:
            client.get()
                    .uri('/api/users/1')
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .expectBody()
                    .jsonPath('id')
                    .isEqualTo(1L)
                    .jsonPath('username')
                    .isEqualTo("user")

    }


    @TestConfiguration
    static class TestConfig {

        def factory = new DetachedMockFactory()

        @Bean
        UserRepository userRepository() {
            return factory.Mock(UserRepository)
        }


    }

}
