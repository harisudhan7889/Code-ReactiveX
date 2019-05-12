package com.hari.transformoperators.model

data class WinningCount(val countryName: String = "",
                        var winningCounts: HashMap<String, Int> = HashMap())
