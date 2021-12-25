package com.example.safeair.Model

data class Building(
    val BuildingAddress: String,
    val BuildingId: Int,
    val BuildingName: String,
    val Floors: List<Floor>,
    val Owner: Any,
    val OwnerId: String
)