package com.example

import com.example.models.data.Stop
import com.example.models.routing.Network
import com.example.pipeline.Pipeline
import com.example.pipeline.steps.LoadRoutes
import com.example.pipeline.steps.LoadStopTimes
import com.example.pipeline.steps.LoadStops
import com.example.pipeline.steps.LoadTrips
import com.example.services.ConnectionBuilder
import com.example.services.Planner
import com.example.util.TimeConverter
import java.io.FileNotFoundException
import kotlin.system.exitProcess

fun main() {
    val pipeline = Pipeline()
        .step(LoadStops("data/simplified/stops.txt"))
        .step(LoadRoutes("data/routes.txt"))
        .step(LoadTrips("data/trips.txt"))
        .step(LoadStopTimes("data/simplified/stop_times.txt"))

    val repositories = try {
        pipeline.run()
    } catch (exception: FileNotFoundException) {
        System.err.println("Error: ${exception.message}")
        exitProcess(1)
    }

    println("stops=${repositories.stops.all().size}")
    println("routes=${repositories.routes.all().size}")
    println("trips=${repositories.trips.all().size}")

    val network: Network = Network(ConnectionBuilder.build(repositories))
    println("connections=${network.connections.size}")

    val fromStop: Stop = repositories.stops.find("Blaha Lujza tér")
    val toStop: Stop = repositories.stops.find("Savoya Park")
    val time: Int = 10 * 60 * 60

    val planner = Planner(network)
    val legs = planner.plan(sourceStop=fromStop, destinationStop=toStop, startTime=time)

    legs?.forEach { leg ->
        println("From: ${leg.fromStop.stopName} -> To: ${leg.toStop.stopName} with ${leg.trip.route.name}")
    } ?: println("${toStop.stopName} is not reachable from ${fromStop.stopName} at ${TimeConverter.timeToText(time)}")
}