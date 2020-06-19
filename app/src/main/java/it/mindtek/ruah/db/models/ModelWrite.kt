package it.mindtek.ruah.db.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import it.mindtek.ruah.db.converters.StringArrayConverter
import it.mindtek.ruah.pojos.Syllable

/**
 * Created by alessandrogaboardi on 21/12/2017.
 */
@Entity(tableName = "write")
open class ModelWrite(
        @PrimaryKey
        var id: String = "",
        var unit_id: Int = 0,
        var word: String = "",
        var type: String = "",
        @Embedded(prefix = "audio_")
        var audio: ModelMedia,
        @Embedded(prefix = "picture_")
        var picture: ModelMedia,
        @TypeConverters(StringArrayConverter::class)
        var letters: MutableList<Syllable> = mutableListOf()
)