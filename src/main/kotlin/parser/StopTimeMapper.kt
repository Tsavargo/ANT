package com.example.parser

import com.example.models.StopTime

class StopTimeMapper: Mapper<StopTime> {
    override fun map(line: Map<String, String>): StopTime {
        val tripID: String = line.getValue("trip_id")
        val stopID: String = line.getValue("stop_id")
        val time: String = line.getValue("arrival_time")
        val stopSequence: Int = line.getValue("stop_sequence").toInt()
        val distance: Float = line.getValue("shape_dist_traveled").toFloat()
        return StopTime(tripID, stopID, time, stopSequence, distance)
    }
}
