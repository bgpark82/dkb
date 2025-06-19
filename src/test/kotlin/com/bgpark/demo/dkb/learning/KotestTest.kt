package com.bgpark.demo.dkb.learning

import com.bgpark.demo.dkb.learning.KotestTest.UserSupplier
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.function.Supplier

class KotestTest {

    @Test
    fun `Custom assertion with function`() {
        val user = UserSupplier()
        user.shouldBeAdult()
    }

    @Test
    fun `Custom assertion with DSL`() {
        val invalidEmail = "bgpark82"
        val user = UserSupplier(email = invalidEmail)
        assertSoftly(user) {
            email should beValidEmail()
        }
    }

    fun User.shouldBeAdult() {
        age shouldBeGreaterThan 18
    }

    fun beValidEmail(): Matcher<String> = Matcher { value ->
        MatcherResult(
            value.contains("@"),
            { "$value is not a valid email" },
            { "$value should not be a valid email" }
        )
    }

    object UserSupplier: Supplier<User> {
        operator fun invoke(
            age: Int =  30,
            name: String = "Peter Park",
            email: String = "bgpark82@gmail.com"
        ): User = User(age = age, name = name, email = email)

        override fun get(): User {
            TODO("Not yet implemented")
        }
    }

    fun createUser(
        age: Int =  30,
        name: String = "Peter Park",
        email: String = "bgpark82@gmail.com"
    ): User = User(age = age, name = name, email = email)

    data class User(
        val age: Int,
        val name: String,
        val email: String
    ) {
        fun isAdult(): Boolean = age > 20
    }
}

class KotestBehaviorTest: BehaviorSpec ({
    Given("a user with age 20") {
        val user = UserSupplier(age = 20)

        When("checking if the user is an adult") {
            val isAdult = user.isAdult()

            Then("the user should be considered an adult") {
                isAdult shouldBe true
            }
        }
    }
})

class KotestDomainModelTest {

    @Test
    fun `recruitment test`() {
        val client = Client()

        val recruitmentV1 = with(client) {
            recruitment {
                title = "기본 채용 공고 제목"
                startDate = LocalDateTime.now()
                endDate = LocalDateTime.now().plusDays(7)
            }
        }

        val recruitmentV2 = client.recruitment {
            title = "Kotlin 백엔드 개발자 채용"
            startDate = LocalDateTime.of(2023, 5, 1, 0, 0)
            endDate = LocalDateTime.of(2023, 5, 15, 0, 0)
        }
    }

    data class Recruitment(
        val id: Long = 0L,
        var title: String,
        var startDate: LocalDateTime,
        var endDate: LocalDateTime
    )

    class Client {

        // recruitment DSL 함수를 Client 클래스의 멤버 함수로 정의합니다.
        // 이렇게 하면 'client.recruitment { ... }' 또는 'with(client) { recruitment { ... } }' 형태로 호출 가능합니다.
        // Recruiter.는 람다 블록이 Recruitment 객체 범위에서 실행될 것. this로 Recruiter 객체에 접근가능하다
        // apply와 같이 사용가능하다
        fun recruitment(block: Recruitment.() -> Unit): Recruitment {
            return Recruitment(
                title = "기본 채용 공고 제목",
                startDate = LocalDateTime.now(),
                endDate = LocalDateTime.now().plusDays(7)
            ).apply(block)
        }

        fun recruitment(): Recruitment {
            return Recruitment(
                title = "기본 채용 공고 (인자 없음)",
                startDate = LocalDateTime.now().minusDays(10), // 다른 기본값
                endDate = LocalDateTime.now().minusDays(3)
            )
        }
    }
}

