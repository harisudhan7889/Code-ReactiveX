package com.hari.transformoperators.model

data class Bike(val isFootPathAvailable: Boolean, val name: String): Vehicle("Bike", name)