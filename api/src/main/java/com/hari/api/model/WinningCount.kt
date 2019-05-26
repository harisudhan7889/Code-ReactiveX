package com.hari.api.model

/**
 * @author Hari Hara Sudhan.N
 */
data class WinningCount(val countryName: String = "",
                        var winningCounts: HashMap<String, Int> = HashMap())
