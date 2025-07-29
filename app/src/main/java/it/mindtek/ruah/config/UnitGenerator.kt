package it.mindtek.ruah.config

import it.mindtek.ruah.db.models.ModelUnit
import it.mindtek.ruah.enums.Category

class UnitGenerator {
    companion object {
        fun getUnits(): MutableList<ModelUnit> = mutableListOf(
            ModelUnit(0, Category.ITALIANO, "presentazione", 1),
            ModelUnit(1, Category.ITALIANO, "lavoro", 2),
            ModelUnit(2, Category.ITALIANO, "cibo", 3),
            ModelUnit(3, Category.ITALIANO, "telefono", 4),
            ModelUnit(4, Category.ITALIANO, "stato", 5),
            ModelUnit(5, Category.ITALIANO, "salute", 6),
            ModelUnit(6, Category.ITALIANO, "citta", 7),
            ModelUnit(7, Category.ITALIANO, "mezzi", 8),
            ModelUnit(8, Category.ITALIANO, "casa", 9),
            ModelUnit(9, Category.ITALIANO, "viaggio", 10),
            ModelUnit(10, Category.SICUREZZA, "cartellonistica", 1),
            ModelUnit(11, Category.SICUREZZA, "rischi_pericoli", 2),
            ModelUnit(12, Category.SICUREZZA, "benessere", 3),
            ModelUnit(13, Category.SICUREZZA, "patente", 4),
            ModelUnit(14, Category.LINGUA, "edilizia_1", 1),
            ModelUnit(15, Category.LINGUA, "edilizia_2", 2),
            ModelUnit(16, Category.LINGUA, "persona", 3),
            ModelUnit(17, Category.LINGUA, "pulizie", 4)
        )
    }
}