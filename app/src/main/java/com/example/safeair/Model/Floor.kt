package com.example.safeair.Model

data class Floor(
    val BuildingId: Int,
    val FloorId: Int,
    val FloorNumber: Int,
    val Rooms: List<Room>,
    val Building: Building
)