package com.example.pipeline.steps

import com.example.models.data.Trip
import com.example.parser.Parser
import com.example.parser.RecordMapper
import com.example.pipeline.Step
import com.example.repositories.Repositories

/** Needs [LoadRoutes] to run first. */
class LoadTrips(private val path: String) : Step {
    override fun run(repositories: Repositories) {
        val mapper = RecordMapper { record ->
            Trip(
                tripId = record.getValue("trip_id"),
                route = repositories.routes.get(record.getValue("route_id"))
            )
        }
        val trips = Parser().read(path, mapper)
        for (trip in trips) {
            repositories.trips.add(trip)
        }
    }
}
