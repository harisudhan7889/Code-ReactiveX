package com.hari.api.utils

import com.hari.api.model.WinningCount
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Hari Hara Sudhan.N
 */
object AppUtils {
    fun getWorldCupWinners(): MutableList<WinningCount> {
        val countries = mutableListOf<WinningCount>()
        val valueArray: Array<String> = arrayOf(
                "West Indies",
                "West Indies",
                "India",
                "Australia",
                "Pakistan",
                "Sri Lanka",
                "Australia",
                "Australia",
                "Australia",
                "India",
                "Australia"
        )

        for (i in 0 until valueArray.size) {
            countries.add(WinningCount(valueArray[i]))
        }
        return countries
    }

    fun isValidMail(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: CharSequence): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }
}