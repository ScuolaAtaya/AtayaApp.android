package it.mindtek.ruah.enums

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import it.mindtek.ruah.R

/**
 * Created by alessandrogaboardi on 04/12/2017.
 */
enum class Category(
    val value: Int,
    @param:StringRes val title: Int,
    @param:DrawableRes val icon: Int,
    @param:StringRes val description: Int,
    @param:RawRes val audio: Int
) {
    UNDERSTAND(
        0,
        R.string.section_understand,
        R.drawable.volume,
        R.string.section_understand_description,
        R.raw.capiamo
    ),
    TALK(
        1,
        R.string.section_talk,
        R.drawable.mic,
        R.string.section_talk_description,
        R.raw.parliamo
    ),
    READ(
        2,
        R.string.section_read,
        R.drawable.eye,
        R.string.section_read_description,
        R.raw.leggiamo
    ),
    WRITE(
        3,
        R.string.section_write,
        R.drawable.edit,
        R.string.section_write_description,
        R.raw.scriviamo
    ),
    FINAL_TEST(
        4,
        R.string.section_final_test,
        R.drawable.edit,
        R.string.section_final_test_description,
        R.raw.verifica_finale
    );

    companion object {
        fun from(value: Int): Category = entries.first {
            it.value == value
        }
    }
}