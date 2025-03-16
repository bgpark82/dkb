package com.bgpark.demo.dkb.learning

import org.junit.jupiter.api.Test

class DelegationTest {

    /**
     * 위임 패턴
     * - 인터페이스를 구현하는 방법 중 하나
     * - 객체의 메서드를 다른 객체에게 위임
     *
     * 장점:
     * 1. 간결성: 코드가 간결하고 명확해짐.
     * 2. 중복 감소: 동일한 기능을 여러 클래스에서 재사용할 때 코드 중복을 줄일 수 있음.
     * 3. 유연성: 객체를 위임하여 기능을 유연하게 결합하고 확장할 수 있음.
     * 4. 상속 대신 사용: 다중 상속의 문제를 피하고, 구성을 통한 유연한 코드 작성 가능.
     *
     * 단점:
     * 1. 세밀한 제어 부족: 위임된 객체의 메서드에 대한 수정이나 확장이 어려울 수 있음.
     * 2. 디버깅 어려움: 호출 흐름이 간접적이어서 디버깅이 어려울 수 있음.
     * 3. 의존성 증가: 위임되는 객체에 의존하게 되어 변경에 영향을 받을 수 있음.
     *
     * 사용처:
     * 1. 기존 구현 재사용: 이미 구현된 클래스를 재사용하고 싶을 때.
     * 2. 복잡한 상속 구조 대신: 다중 상속 대신 구성(Composition)을 사용하고 싶을 때.
     * 3. 기능 확장: 외부 API나 라이브러리의 기능을 변경하지 않고 확장할 때.
     * 4. 상속을 피하고 싶을 때: 객체 지향에서 상속 대신 위임을 통해 유연한 구조를 만들고 싶을 때.
     */

    @Test
    fun `delegation 적용`() {
        val base = BaseImpl(10)
        DerivedInKotlin(base).print() // 10
    }

    @Test
    fun `delegation 미적용`() {
        val base = BaseImpl(10)
        DerivedInJava(base).print() // 10
    }

    interface Base {
        fun print()
    }

    class BaseImpl(val x: Int) : Base {
        override fun print() { print(x) }
    }

    // b: Base 타입의 객체
    // 즉, Derived가 받은 b 객체에게 메소드를 호출하여 일을 시킨다
    // Derived는 Base를 구현할 필요없이 Base를 구현한 b의 메소드를 직접 사용가능
    // bolierplate 제거
    class DerivedInKotlin(b : Base) : Base by b

    class DerivedInJava(val b: Base) : Base {
        override fun print() { b.print() }
    }
}