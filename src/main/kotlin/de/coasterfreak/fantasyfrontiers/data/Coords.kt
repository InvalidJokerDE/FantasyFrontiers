package de.coasterfreak.fantasyfrontiers.data

import kotlinx.serialization.Serializable

@Serializable
data class Coords(
    val x: Double = 0.0,
    val y: Double = 0.0
)