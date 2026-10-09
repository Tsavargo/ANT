package com.example.parser

import com.example.models.Color
import com.example.models.Route
import com.example.models.RouteType

class RouteMapper: Mapper<Route> {
    override fun map(line: Map<String, String>): Route {
        val agencyID: String = line.getValue("agency_id")
        val routeID: String = line.getValue("route_id")
        val shortName: String = line.getValue("route_short_name")
        val longName: String = line.getValue("route_long_name")
        val type: RouteType = RouteType.fromCode(line.getValue("route_type").toInt())
        val color: Color = Color.fromHex(line.getValue("route_color"))
        return Route(agencyID, routeID, shortName, longName, type, color)
    }
}
