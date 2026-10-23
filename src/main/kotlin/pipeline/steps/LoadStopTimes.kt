package com.example.pipeline.steps

import com.example.models.data.StopTime
import com.example.parser.Parser
import com.example.parser.RecordMapper
import com.example.pipeline.Step
import com.example.repositories.Repositories
import com.example.util.TimeConverter

class LoadStopTimes(private val path: String) : Step {
    override fun run(repositories: Repositories) {
        val mapper = RecordMapper { record ->
            StopTime(
                tripId = record.getValue("trip_id"),
                stopId = record.getValue("stop_id"),
                sequence = record.getValue("stop_sequence").toInt(),
                arrival = TimeConverter.textToTime(record.getValue("arrival_time")),
                departure = TimeConverter.textToTime(record.getValue("departure_time"))
            )
        }
        val stopTimes = Parser().read(path, mapper)
        for (stopTime in stopTimes) {
            repositories.stopTimes.add(stopTime)
        }
    }
}
