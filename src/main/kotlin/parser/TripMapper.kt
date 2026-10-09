package com.example.parser

import com.example.models.Trip

class TripMapper: Mapper<Trip> {
    override fun map(line: Map<String, String>): Trip {
        val routeID: String = line.getValue("route_id")
        val tripID: String = line.getValue("trip_id")
        val serviceID: String = line.getValue("service_id")
        val headSign: String = line.getValue("trip_headsign")
        return Trip(routeID, tripID, serviceID, headSign)
    }
}
