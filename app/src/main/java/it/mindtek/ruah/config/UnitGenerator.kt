package it.mindtek.ruah.config

import it.mindtek.ruah.db.models.ModelUnit

/**
 * Created by alessandrogaboardi on 29/11/2017.
 */
class UnitGenerator {
    companion object {
        fun getUnits(): MutableList<ModelUnit> {
            val units: MutableList<ModelUnit> = mutableListOf()
            units.add(ModelUnit(0, "accoglienza", 1))
            units.add(ModelUnit(1, "lavoro", 2))
            units.add(ModelUnit(2, "cibo", 3))
            units.add(ModelUnit(3, "telefono", 4))
            units.add(ModelUnit(4, "stato", 5))
            units.add(ModelUnit(5, "salute", 6))
            units.add(ModelUnit(6, "citta", 7))
            units.add(ModelUnit(7, "mezzi", 8))
            units.add(ModelUnit(8, "casa", 9))
            units.add(ModelUnit(9, "viaggio", 10))
            units.add(ModelUnit(10, "cartellonistica", 11))
            units.add(ModelUnit(11, "rischi_pericoli", 12))
            units.add(ModelUnit(12, "benessere", 13))
            units.add(ModelUnit(13, "patente", 14))
            return units
        }
    }
}