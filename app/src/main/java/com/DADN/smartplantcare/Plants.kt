package com.DADN.smartplantcare

data class Plants(val plantId: String = "",
                  val plantName: String = "",
                  val minSoilMoisture: Int = 0,
                  val maxSoilMoisture: Int = 0,
                  val temperature: Int = 0,
                  val humidity: Int = 0) {

}