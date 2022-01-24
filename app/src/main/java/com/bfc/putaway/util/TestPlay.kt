package com.bfc.putaway.util

import kotlinx.coroutines.*

fun main() {
//    val one = somethingUsefulOneAsync()
//        runBlocking { // this: CoroutineScope
////            cancelationCoprative()
//            println("dtat final is ${one.await()}")
//    }

//    val list = mutableListOf(1, 2, 3)
//    list.swap(0, 3) // 'this' inside 'swap()' will hold the value of 'list'
//
//    for (itm in list){
//        println("dtat list is: $itm")
//    }

    var str = "Hello World"
//    str.let { println("${it.plus("New added")} !!!") }
    str = run {
        val str = "New updated String"
        str
    }

    println(str)

}

fun MutableList<Int>.swap(index1: Int, index2: Int) {
    val tmp = this[index1] // 'this' corresponds to the list
    this[index1] = this[index2]
    this[index2] = tmp
}

//fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
//    val tmp = this[index1] // 'this' corresponds to the list
//    this[index1] = this[index2]
//    this[index2] = tmp
//}

fun somethingUsefulOneAsync() = GlobalScope.async {
    doSomething(12345)
}

suspend fun cancelationCoprative() = coroutineScope {
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrintTime = startTime
        var i = 0
//        while (isActive) { // cancellable computation loop
        while (i < 15) { // computation loop, just wastes CPU
            // print a message twice a second
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("job: I'm sleeping ${i++} ...")
                nextPrintTime += 500L
            }
        }
    }
    delay(1300L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}

// job is cancelable
suspend fun cancelJob() =  coroutineScope{
    val job = launch {
        repeat(1000) { i ->
            println("job: I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(3000L) // delay a bit
    println("main: I'm tired of waiting!")
    job.cancel() // cancels the job
    job.join() // waits for job's completion
    println("main: Now I can quit.")
}

// Concurrently executes both sections
suspend fun doWorld() = coroutineScope { // this: CoroutineScope
    launch {
        delay(1500L)
        println("World 2")
        doSomething(1234)
    }
    launch {
        delay(1000L)
        println("World 1")
        doSomething(23456)
    }
    println("Hello")
}


suspend fun doSomething(number: Int): String{
    var count = 0
    var num = number
    var squareNumber = number
    var finalOut = ""

//     var newNumber = number

    while(num > 0){
        num /= 10
        ++count
    }
    println("count is: "+ count)
    for(i in 0 until count){
        var balance = squareNumber % 10
        var divided = squareNumber / 10
        squareNumber = divided

        var sqr = balance * balance
        finalOut = "$sqr$finalOut"

        println("balance is: $balance and $divided and final $finalOut")
    }

    return finalOut

}