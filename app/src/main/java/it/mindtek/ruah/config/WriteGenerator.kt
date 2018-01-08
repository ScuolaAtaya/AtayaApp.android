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
                mutableListOf(Syllable(0,"a"), Syllable(1,"u"), Syllable(2,"r"), Syllable(3,"i"), Syllable(4,"c"),Syllable(5,"o"), Syllable(6,"l"), Syllable(7,"a"), Syllable(8,"r"), Syllable(9,"i"))
        )
        writes.add(write1)
        return writes
    }
}