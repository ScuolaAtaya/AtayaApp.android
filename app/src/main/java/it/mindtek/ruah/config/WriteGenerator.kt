package it.mindtek.ruah.config

import it.mindtek.ruah.db.models.ModelWrite
import it.mindtek.ruah.pojos.Syllable

/**
 * Created by alessandrogaboardi on 08/01/2018.
 */
object WriteGenerator {
    fun getWrites() : MutableList<ModelWrite>{
        val writes = mutableListOf<ModelWrite>()
        val write1 = ModelWrite(
                0,
                0,
                "",
                "auricolari",
                mutableListOf(Syllable(0,"au"), Syllable(1,"r"), Syllable(2,"i"), Syllable(3,"co"), Syllable(4,"la"),Syllable(5,"ri"))
        )
        writes.add(write1)
        return writes
    }
}