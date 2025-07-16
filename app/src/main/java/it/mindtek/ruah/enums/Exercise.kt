package it.mindtek.ruah.enums

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import it.mindtek.ruah.R

/**
 * Created by alessandrogaboardi on 04/12/2017.
 */
enum class Exercise(
    val value: Int,
    @param:StringRes val title: Int,
    @param:DrawableRes val icon: Int,
    @param:StringRes val description: Int,
    @param:RawRes val audio: Int
) {
    UNDERSTAND(
        value = 0,
        title = R.string.section_understand,
        icon = R.drawable.volume,
        description = R.string.section_understand_description,
        audio = R.raw.capiamo
    ),
    TALK(
        value = 1,
        title = R.string.section_talk,
        icon = R.drawable.mic,
        description = R.string.section_talk_description,
        audio = R.raw.parliamo
    ),
    READ(
        value = 2,
        title = R.string.section_read,
        icon = R.drawable.eye,
        description = R.string.section_read_description,
        audio = R.raw.leggiamo
    ),
    WRITE(
        value = 3,
        title = R.string.section_write,
        icon = R.drawable.edit,
        description = R.string.section_write_description,
        audio = R.raw.scriviamo
    ),
    FINAL_TEST(
        value = 4,
        title = R.string.section_final_test,
        icon = R.drawable.edit,
        description = R.string.section_final_test_description,
        audio = R.raw.verifica_finale
    );

    companion object {
        fun from(value: Int): Exercise = entries.first {
            it.value == value
        }
    }
}