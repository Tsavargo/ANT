package com.example.pipeline.steps

import com.example.models.data.StopTime
import com.example.parser.Parser
import com.example.parser.RecordMapper
import com.example.pipeline.Step
import com.example.repositories.Repositories

class LoadStopTimes(private val path: String) : Step {
    override fun run(repositories: Repositories) {
        val mapper = RecordMapper { record ->
            StopTime(
                tripId = record.getValue("trip_id"),
                stopId = record.getValue("stop_id"),
                sequence = record.getValue("stop_sequence").toInt(),
                arrival = parseTime(record.getValue("arrival_time")),
                departure = parseTime(record.getValue("departure_time"))
            )
        }
        val stopTimes = Parser().read(path, mapper)
        for (stopTime in stopTimes) {
            repositories.stopTimes.add(stopTime)
        }
    }

    /** "HH:MM:SS" to seconds since midnight. */
    private fun parseTime(time: String): Int {
        val (hour, minute, second) = time.trim().split(":").map { it.toInt() }
        return hour * 3600 + minute * 60 + second
    }
}
