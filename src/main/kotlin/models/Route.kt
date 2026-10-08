package com.example.models

data class Route(
    val agencyID: String,
    val routeID: String,
    val shortName: String,
    val longName: String,
    val type: RouteType,
    val color: Color) {

}