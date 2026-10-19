package com.example.repositories

import com.example.models.data.Route

class RouteRepository {
    private val routes = mutableMapOf<String, Route>()

    fun add(route: Route) {
        routes[route.routeId] = route
    }

    fun get(routeId: String): Route = routes[routeId] ?: error("Unknown Route: $routeId")

    fun all(): Collection<Route> = routes.values
}