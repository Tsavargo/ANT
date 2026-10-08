package com.example.parser

import com.example.models.Stop

class StopMapper: Mapper<Stop> {
    override fun map(line: Map<String, String>): Stop {
        val stopID: String = line.getValue("stop_id")
        val stopName: String = line.getValue("stop_name")
        val latitude: Double = line.getValue("stop_lat").toDouble()
        val longitude: Double = line.getValue("stop_lon").toDouble()
        return Stop(stopID, stopName, latitude, longitude)
    }
}