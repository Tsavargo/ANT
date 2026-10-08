package com.example.models

data class StopTime(
    val tripID: String,
    val stopID: String,
    val time: String,
    val stopSequence: Int,
    val distance: Float) {

}