package it.mindtek.ruah.config

import it.mindtek.ruah.db.models.ModelUnit

class UnitGenerator {
    companion object {
        fun getUnits(): MutableList<ModelUnit> = mutableListOf(
            ModelUnit(0, "italiano", "accoglienza", 1),
            ModelUnit(1, "italiano", "lavoro", 2),
            ModelUnit(2, "italiano", "cibo", 3),
            ModelUnit(3, "italiano", "telefono", 4),
            ModelUnit(4, "italiano", "stato", 5),
            ModelUnit(5, "italiano", "salute", 6),
            ModelUnit(6, "italiano", "citta", 7),
            ModelUnit(7, "italiano", "mezzi", 8),
            ModelUnit(8, "italiano", "casa", 9), // TODO name
            ModelUnit(9, "italiano", "viaggio", 10),
            ModelUnit(10, "sicurezza", "cartellonistica", 1),
            ModelUnit(11, "sicurezza", "rischi_pericoli", 2),
            ModelUnit(12, "sicurezza", "benessere", 3),
            ModelUnit(13, "sicurezza", "patente", 4),
            ModelUnit(14, "lingua", "edilizia_1", 1), // TODO name
            ModelUnit(15, "lingua", "edilizia_2", 2), // TODO name
            ModelUnit(16, "lingua", "persona", 3),
            ModelUnit(17, "lingua", "pulizie", 4) // TODO name
        )
    }
}