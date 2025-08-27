package com.bgpark.demo.dkb.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.web.servlet.function.ServerResponse.async
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.awaitAll

class CoroutineTest {


}

fun main() {
    // You can call suspend functions from within runBlocking
    runBlocking(Dispatchers.IO) {
        println("메인 루틴 시작: ${Thread.currentThread().name}")

        // launch를 사용해 새로운 코루틴을 시작합니다.
        launch {
            /** CHAPTER 01: 동기 vs 비동기
            //  doSomethingSync()
            //  doSomethingAsync()
            //  doSomethingAsync()
            */

            /** CHAPTER 02: 코루틴 컨텍스트와 디스패처
            val ioJob = ioJob() // 네트워크 호출을 흉내내는 코루틴. I/O 디스패처를 사용합니다.
            val cpuJob = cpuJob() // 복잡한 연산을 흉내내는 코루틴. Default 디스패처를 사용합니다.
            // 두 작업이 모두 완료될 때까지 기다립니다.
            ioJob.join()
            cpuJob.join()
            */

            /** CHAPTER 03: 코루틴 병렬 처리
             */
            val time = measureTimeMillis {
                // 서로 의존하지 않는 두 비동기 작업을 async로 병렬 시작
                val apiCall1 = async { fetchUser() }
                val apiCall2 = async { fetchOrders() }
                // awaitAll()을 사용하여 모든 작업이 끝날 때까지 기다립니다.
                val results = listOf(apiCall1, apiCall2).awaitAll()
                println(results)
            }
            println("총 실행 시간: $time ms")
            println("모든 작업 완료: ${Thread.currentThread().name}")
        }

        // 메인 루틴은 launch가 끝날 때까지 기다리지 않고 다음 줄을 실행합니다.
        println("메인 루틴은 계속 진행됩니다.")
    }
}

suspend fun fetchUser(): String {
    println("네트워크 지연 가정: ${Thread.currentThread().name}")
    delay(1000) // 네트워크 지연 가정
    return "User: Alice"
}

suspend fun fetchOrders(): String {
    println("네트워크 지연 가정: ${Thread.currentThread().name}")
    delay(1500) // 네트워크 지연 가정
    return "Orders: [Phone, Laptop]"
}

private fun CoroutineScope.cpuJob(): Job = launch(Dispatchers.Default) {
        println("CPU 작업 시작: ${Thread.currentThread().name}")
        // 무거운 연산 (1억 번 반복)
        (1..1_000_000_000).sumOf { it.toLong() }
        println("CPU 작업 완료")
}

private fun CoroutineScope.ioJob(): Job = launch(Dispatchers.IO) {
        println("I/O 작업 시작: ${Thread.currentThread().name}")
        delay(1500) // 비동기 네트워크 호출을 가정
        println("I/O 작업 완료")
}

suspend fun doSomethingAsync() {
    println("비동기 작업 시작: ${Thread.currentThread().name}")
    // delay는 스레드를 블록하지 않고 코루틴을 1초간 일시 정지시킵니다.
    delay(1000)
    println("비동기 작업 완료")
}

fun doSomethingSync() {
    println("동기 작업 시작: ${Thread.currentThread().name}")
    // delay는 스레드를 블록하지 않고 코루틴을 1초간 일시 정지시킵니다.
    Thread.sleep(1000)
    println("동기 작업 완료")
}