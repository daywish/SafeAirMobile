package com.example.safeair.Model

data class Room(
    val Conditioner: Conditioner,
    val ConditionerId: Int,
    val Devices: Devices,
    val FloorId: Int,
    val Requests: List<Any>,
    val RoomId: Int,
    val RoomNumber: Int,
    val RoomTemperature: Double,
    val RoomWetness: Double,
    val Floor: Floor
)