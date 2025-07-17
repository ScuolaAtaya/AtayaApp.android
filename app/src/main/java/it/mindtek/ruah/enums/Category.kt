package it.mindtek.ruah.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import it.mindtek.ruah.R

enum class Category(
    @param:StringRes val funded: Int? = null,
    @param:DrawableRes val fundedIcon: Int? = null,
    @param:StringRes val fundedAlt: Int? = null,
    @param:StringRes val cofunded: Int? = null,
    @param:DrawableRes val cofundedIcon: Int? = null,
    @param:StringRes val cofundedAlt: Int? = null
) {
    ITALIANO,
    SICUREZZA(
        funded = R.string.sicurezza_funded,
        cofunded = R.string.sicurezza_cofunded,
        cofundedIcon = R.drawable.ue,
        cofundedAlt = R.string.sicurezza_cofunded_alt
    ),
    LINGUA(
        funded = R.string.lingua_funded,
        fundedIcon = R.drawable.ue,
        fundedAlt = R.string.lingua_funded_alt,
        cofunded = R.string.lingua_cofunded,
        cofundedIcon = R.drawable.brick,
        cofundedAlt = R.string.lingua_cofunded_alt
    );
}