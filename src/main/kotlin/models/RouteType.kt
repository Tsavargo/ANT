package com.example.models

enum class RouteType(val code: Int) {
    TRAM(0),
    METRO(1),
    RAIL(2),
    BUS(3),
    FERRY(4),
    TROLLEY(11),
    HEV(109);

    companion object {
        fun fromCode(code: Int): RouteType {
            return when (code) {
                0 -> TRAM
                1 -> METRO
                2 -> RAIL
                3 -> BUS
                4 -> FERRY
                11 -> TROLLEY
                109 -> HEV
                else -> throw IllegalArgumentException("Invalid route type: $code")
            }
        }
    }
}