package encode

import java.lang.Math.log
import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.and

/**
 * User: zhaoyao
 * Date: 11-9-24
 * Time: PM10:48
 */
object VariableByteCode {

    private fun encodeNumber(n: Int): ByteArray {
        var n = n
        if (n == 0) {
            return byteArrayOf(0)
        }
        val i = (log(n.toDouble()) / log(128.0)).toInt() + 1
        val rv = ByteArray(i)
        var j = i - 1
        do {
            rv[j--] = (n % 128).toByte()
            n /= 128
        } while (j >= 0)
        rv[i - 1] = (rv[i - 1] + 128).toByte()
        return rv
    }

    fun encode(numbers: List<Int>): ByteArray {
        val buf = ByteBuffer.allocate(numbers.size * (Integer.SIZE / java.lang.Byte.SIZE))
        for (number in numbers) {
            buf.put(encodeNumber(number))
        }
        buf.flip()
        val rv = ByteArray(buf.limit())
        buf.get(rv)
        return rv
    }

    fun decode(byteStream: ByteArray): List<Int> {
        val numbers = ArrayList<Int>()
        var n = 0
        for (b in byteStream) {
            if (b and 0xff.toByte() < 128) {
                n = 128 * n + b
            } else {
                val num = 128 * n + (b - 128 and 0xff)
                numbers.add(num)
                n = 0
            }
        }
        return numbers
    }

    /**
     * 对于已经排序好的数字序列，不再对原始值进行编码，而是对与前一个值的差值进行编码
     *
     *
     * [ 1, 2, 3, 4, 5, 6, 7  ]
     * -->  [1, 1, 1, 1, 1, 1, 1]f
     *
     * @param numbers
     * @return
     */
    fun encodeInterpolate(numbers: List<Int>): ByteArray {
        val buf = ByteBuffer.allocate(numbers.size * (Integer.SIZE / java.lang.Byte.SIZE))
        var last = -1
        for (i in numbers.indices) {
            val num = numbers[i]
            if (i == 0) {
                buf.put(encodeNumber(num))
            } else {
                buf.put(encodeNumber(num - last))
            }
            last = num
        }

        for (number in numbers) {
            buf.put(encodeNumber(number))
        }
        buf.flip()
        val rv = ByteArray(buf.limit())
        buf.get(rv)
        return rv
    }

    fun decodeInterpolate(byteStream: ByteArray): List<Int> {
        val numbers = ArrayList<Int>()
        var n = 0
        var last = -1
        var notFirst = false
        for (b in byteStream) {
            if (b and 0xff.toByte() < 128) {
                n = 128 * n + b
            } else {
                val num: Int
                if (notFirst) {
                    num = last + (128 * n + (b - 128 and 0xff))

                } else {
                    num = 128 * n + (b - 128 and 0xff)
                    notFirst = true
                }
                last = num
                numbers.add(num)
                n = 0
            }
        }
        return numbers
    }


}