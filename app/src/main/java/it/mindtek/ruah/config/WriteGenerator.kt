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
                "basic",
                mutableListOf(Syllable(0,"au", mutableListOf(0)), Syllable(1,"ri", mutableListOf(1, 4)), Syllable(3,"co", mutableListOf(2)), Syllable(4,"la", mutableListOf(3)),Syllable(5,"ri", mutableListOf(4, 1)))
        )
        writes.add(write1)
        val write2 = ModelWrite(
                1,
                0,
                "",
                "pimpiripettenusa",
                "advanced"
        )
        writes.add(write2)
        return writes
    }
}