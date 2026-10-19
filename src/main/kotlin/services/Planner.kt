package com.example.services

import com.example.models.data.Stop
import com.example.models.routing.Leg
import com.example.models.routing.Network

class Planner(val network: Network, val minTransferTime: Int) {
    fun plan(sourceStop: Stop, destinationStop: Stop, startTime: Int): List<Leg>? {
        // TODO: implement Connection Scan Algorithm
        return null
    }
}