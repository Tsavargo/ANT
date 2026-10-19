package com.example.pipeline.steps

import com.example.models.data.Stop
import com.example.parser.Parser
import com.example.parser.RecordMapper
import com.example.pipeline.Step
import com.example.repositories.Repositories

class LoadStops(private val path: String) : Step {
    override fun run(repositories: Repositories) {
        val mapper = RecordMapper { record ->
            Stop(
                stopId = record.getValue("stop_id"),
                stopName = record.getValue("stop_name"),
                latitude = record.getValue("stop_lat").toDouble(),
                longitude = record.getValue("stop_lon").toDouble()
            )
        }
        val stops = Parser().read(path, mapper)
        for (stop in stops) {
            repositories.stops.add(stop)
        }
    }
}
