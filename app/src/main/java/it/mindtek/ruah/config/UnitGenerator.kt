package it.mindtek.ruah.config

import it.mindtek.ruah.db.models.ModelCategory
import it.mindtek.ruah.db.models.ModelUnit

class UnitGenerator {
    companion object {
        fun getCategories(): MutableList<ModelCategory> = mutableListOf(
            ModelCategory(0, "italiano", 1),
            ModelCategory(1, "sicurezza", 2),
            ModelCategory(2, "lingua", 9),
        )

        fun getUnits(): MutableList<ModelUnit> = mutableListOf(
            ModelUnit(id = 0, categoryId = 0, name = "accoglienza", position = 1),
            ModelUnit(id = 1, categoryId = 0, name = "lavoro", position = 1),
            ModelUnit(id = 2, categoryId = 0, name = "cibo", position = 3),
            ModelUnit(id = 3, categoryId = 0, name = "telefono", position = 4),
            ModelUnit(id = 4, categoryId = 0, name = "stato", position = 2),
            ModelUnit(id = 5, categoryId = 0, name = "salute", position = 10),
            ModelUnit(id = 6, categoryId = 0, name = "citta", position = 5),
            ModelUnit(id = 7, categoryId = 0, name = "mezzi", position = 6),
            ModelUnit(id = 8, categoryId = 0, name = "casa", position = 12), // TODO name
            ModelUnit(id = 9, categoryId = 0, name = "viaggio", position = 8),
            ModelUnit(id = 10, categoryId = 1, name = "cartellonistica", position = 2),
            ModelUnit(id = 11, categoryId = 1, name = "rischi_pericoli", position = 3),
            ModelUnit(id = 12, categoryId = 1, name = "benessere", position = 4),
            ModelUnit(id = 13, categoryId = 1, name = "patente", position = 7),
            ModelUnit(id = 14, categoryId = 2, name = "edilizia_1", position = 1), // TODO name
            ModelUnit(id = 15, categoryId = 2, name = "edilizia_2", position = 2), // TODO name
            ModelUnit(id = 16, categoryId = 2, name = "persona", position = 11),
            ModelUnit(id = 17, categoryId = 2, name = "pulizie", position = 12) // TODO name
        )
    }
}