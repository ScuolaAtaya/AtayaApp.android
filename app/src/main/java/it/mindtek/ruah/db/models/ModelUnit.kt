package it.mindtek.ruah.db.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
@Entity(tableName = "units")
open class ModelUnit(
        @PrimaryKey
        var id: Int = 0,
        @DrawableRes
        var icon: Int = 0,
        @StringRes
        var name: Int = 0,
        var color: Int = 0,
        var colorDark: Int = 0,
        var position: Int = 0,
        var advanced: Boolean = false,
        var enabled: Boolean = false
) {}