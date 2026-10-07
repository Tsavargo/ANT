package com.example.models

@JvmInline
value class Color(
    val rgb: Int,
) {
    init {
        require(rgb in 0..0xFFFFFF) {
            "RGB must fit in 24 bits, was 0x${rgb.toHexString()}"
        }
    }

    fun toHex(): String {
        return rgb.toHexString(HEX_FORMAT)
    }

    override fun toString(): String {
        return "#${toHex()}"
    }

    companion object {
        private val HEX_FORMAT = HexFormat {
            upperCase = true
            number {
                removeLeadingZeros = true
                minLength = 6
            }
        }

        fun fromHex(hex: String): Color {
            val clean = hex.trim().removePrefix("#")
            require(clean.length == 6) {
                "Expected 6 hex digits, got '$hex'"
            }
            return Color(clean.hexToInt())
        }
    }
}
