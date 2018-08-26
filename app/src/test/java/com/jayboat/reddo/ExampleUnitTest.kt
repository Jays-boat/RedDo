package com.jayboat.reddo

import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val ymd = arrayOf(0, 0, 0)
        var sb = StringBuilder()
        "2017y25d".forEach {
            if (sb.isBlank()) {
                if (it.isDigit()) {
                    sb.append(it)
                }
                return@forEach
            }
            when (it) {
                'y' -> {
                    ymd[0] = sb.toString().toInt()
                    sb = StringBuilder()
                }
                'm' -> {
                    ymd[1] = sb.toString().toInt()
                    sb = StringBuilder()
                }
                'd' -> {
                    ymd[2] = sb.toString().toInt()
                    sb = StringBuilder()
                }
                else -> sb.append(it)
            }
        }
        println("${ymd[0]},${ymd[1]},${ymd[2]}")

    }

    fun String.do1(): String {
        println("is do1")
        return "do1"
    }

    fun String.do2(): String {
        println("is do2")
        return "do2"
    }

}
