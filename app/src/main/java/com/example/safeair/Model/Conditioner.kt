package com.example.safeair.Model

data class Conditioner(
    val ConditionerCost: Double,
    val ConditionerId: Int,
    val ConditionerName: String,
    val Rooms: List<Any>,
    val ServiceTime: String
)