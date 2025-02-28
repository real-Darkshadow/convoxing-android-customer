package com.convoxing.convoxing_customer.utils

import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.models.DeepLinkValues

object DeepLinkHandler {

    fun getDeepLinkId(deepLinkValues: DeepLinkValues): Int {
        return when (deepLinkValues) {
            DeepLinkValues.HOME -> R.id.homeFragment
            DeepLinkValues.PLANS -> R.id.homeFragment
            DeepLinkValues.PRACTICE -> R.id.practiceFragment
            DeepLinkValues.PROFILE -> R.id.progressFragment
            DeepLinkValues.LEADERBOARD -> R.id.leagueFragment
        }
    }
}