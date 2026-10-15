package com.example.services

import com.example.models.Leg
import com.example.models.Stop
import com.example.repositories.Network

class Planner(val network: Network, val minTransferTime: Int) {
    fun plan(sourceStop: Stop, destinationStop: Stop, startTime: Int): List<Leg>? {
        // TODO: implement Connection Scan Algorithm
        return null
    }
}