package com.example.pipeline.steps

import com.example.models.data.Color
import com.example.models.data.Route
import com.example.models.data.RouteType
import com.example.parser.Parser
import com.example.parser.RecordMapper
import com.example.pipeline.Step
import com.example.repositories.Repositories

class LoadRoutes(private val path: String) : Step {
    override fun run(repositories: Repositories) {
        val mapper = RecordMapper { record ->
            Route(
                routeId = record.getValue("route_id"),
                name = record.getValue("route_short_name"),
                type = RouteType.fromCode(record.getValue("route_type").toInt()),
                color = Color.fromHex(record.getValue("route_color"))
            )
        }
        val routes = Parser().read(path, mapper)
        for (route in routes) {
            repositories.routes.add(route)
        }
    }
}
