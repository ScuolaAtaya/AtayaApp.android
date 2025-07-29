package it.mindtek.ruah.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import it.mindtek.ruah.R

enum class Category(
    @param:DrawableRes val firstFundedIcon: Int? = null,
    @param:StringRes val firstFundedAlt: Int? = null,
    @param:StringRes val firstFunded: Int? = null,
    @param:DrawableRes val secondFundedIcon: Int? = null,
    @param:StringRes val secondFundedAlt: Int? = null,
    @param:StringRes val secondFunded: Int? = null,
    @param:DrawableRes val thirdFundedIcon: Int? = null,
    @param:StringRes val thirdFundedAlt: Int? = null
) {
    ITALIANO,
    SICUREZZA(
        firstFunded = R.string.funded_spelling,
        secondFundedIcon = R.drawable.ministero_interno,
        secondFundedAlt = R.string.ministero_interno_alt,
        secondFunded = R.string.cofunded_ministero_interno,
        thirdFundedIcon = R.drawable.ue,
        thirdFundedAlt = R.string.ue_alt
    ),
    LINGUA(
        firstFundedIcon = R.drawable.brick,
        firstFundedAlt = R.string.brick_alt,
        firstFunded = R.string.erasmus_brick,
        secondFundedIcon = R.drawable.ue,
        secondFundedAlt = R.string.ue_alt,
        secondFunded = R.string.funded_ue
    );
}