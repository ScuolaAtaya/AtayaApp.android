package it.mindtek.ruah.config

import it.mindtek.ruah.db.models.ModelCategory
import it.mindtek.ruah.db.models.ModelUnit

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
class UnitGenerator {
    companion object {
        fun getCategories(): MutableList<ModelCategory> = mutableListOf(
            ModelCategory(0, "presentazione", 1),
            ModelCategory(1, "lavoro", 2),
            ModelCategory(2, "edilizia", 9),
        )

        fun getUnits(): MutableList<ModelUnit> = mutableListOf(
            ModelUnit(id = 0, categoryId = 0, name = "accoglienza", position = 1),
            ModelUnit(id = 1, categoryId = 0, name = "stato", position = 2),
            ModelUnit(id = 2, categoryId = 1, name = "generico", position = 1),
            ModelUnit(id = 3, categoryId = 1, name = "cartellonistica", position = 2),
            ModelUnit(id = 4, categoryId = 1, name = "rischi_pericoli", position = 3),
            ModelUnit(id = 5, categoryId = 1, name = "benessere", position = 4),
            ModelUnit(id = 6, name = "cibo", position = 3),
            ModelUnit(id = 7, name = "telefono", position = 4),
            ModelUnit(id = 8, name = "citta", position = 5),
            ModelUnit(id = 9, name = "mezzi", position = 6),
            ModelUnit(id = 10, name = "patente", position = 7),
            ModelUnit(id = 11, name = "viaggio", position = 8),
            ModelUnit(id = 12, categoryId = 2, name = "edilizia_1", position = 1), // TODO name
            ModelUnit(id = 13, categoryId = 2, name = "edilizia_2", position = 2), // TODO name
            ModelUnit(id = 14, name = "salute", position = 10),
            ModelUnit(id = 15, name = "persona", position = 11),
            ModelUnit(id = 16, name = "casa", position = 12), // TODO name
            ModelUnit(id = 17, name = "pulizie", position = 12) // TODO name
        )
    }
}