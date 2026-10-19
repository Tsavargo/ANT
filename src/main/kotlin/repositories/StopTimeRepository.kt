package com.example.repositories

import com.example.models.data.StopTime

/** Stores the stop times grouped by trip, so all stops of a trip can be looked up by its trip id. */
class StopTimeRepository {
    private val tripStopTimes = mutableMapOf<String, MutableList<StopTime>>()

    fun add(stopTime: StopTime) {
        if (stopTime.tripId !in tripStopTimes) {
            tripStopTimes[stopTime.tripId] = mutableListOf()
        }
        tripStopTimes.getValue(stopTime.tripId).add(stopTime)
    }

    /** The stop times of one trip, in the order the vehicle visits them. */
    fun forTrip(tripId: String): List<StopTime> {
        val list = tripStopTimes[tripId] ?: return emptyList()
        return list.sortedBy { it.sequence }
    }
}