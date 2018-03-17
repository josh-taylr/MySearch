package encode

import kotlin.experimental.and
import kotlin.experimental.or

object VariableByte : NumberEncoder {

    private const val MAX_BYTE = 0b10000000
    private const val MAX_BYTE_MASK: Byte = 0b01111111

    override fun encode(numbers: Iterable<Long>): List<Byte> = numbers.flatMap(this::encode)

    override fun decode(bytes: Iterable<Byte>): List<Long> {
        val numbers = mutableListOf<Long>()
        var n = 0L
        bytes.forEach { byte ->
            if (!byte.highOrderBit()) {
                n = MAX_BYTE * n + byte
            } else {
                n = MAX_BYTE * n + byte.withHighOrderBit(false)
                numbers.add(n)
                n = 0L
            }
        }
        return numbers
    }

    internal fun encode(number: Long): List<Byte> {
        var num = number
        val bytes = mutableListOf<Byte>()
        do {
            bytes += (num % MAX_BYTE).toByte()
            num /= MAX_BYTE
        } while (num > 0)
        bytes.reverse()
        bytes[bytes.lastIndex] = bytes[bytes.lastIndex].withHighOrderBit(true)
        return bytes
    }

    private fun Byte.highOrderBit(): Boolean = (this and MAX_BYTE.toByte()).toInt() != 0

    private fun Byte.withHighOrderBit(high: Boolean): Byte = when (high) {
        true -> this or MAX_BYTE.toByte()
        false -> this and  MAX_BYTE_MASK
    }
}
