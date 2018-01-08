package it.mindtek.ruah.config

import it.mindtek.ruah.R
import it.mindtek.ruah.db.models.ModelSpeak

/**
 * Created by alessandrogaboardi on 18/12/2017.
 */
object SpeakGenerator {
    fun getSpeaks(): MutableList<ModelSpeak>{
        val speaks: MutableList<ModelSpeak> = mutableListOf()
        val speak1 = ModelSpeak(
                0,
                0,
                "http://www.xsjjys.com/data/out/51/WHDQ-511971771.jpg",
                ""
        )
        speaks.add(speak1)
        val speak2 = ModelSpeak(
                1,
                0,
                "http://www.xsjjys.com/data/out/51/WHDQ-511971756.jpg",
                ""
        )
        speaks.add(speak2)
        val speak3 = ModelSpeak(
                2,
                0,
                "http://www.xsjjys.com/data/out/51/WHDQ-511971723.jpg",
                ""
        )
        speaks.add(speak3)
        val speak4 = ModelSpeak(
                3,
                0,
                "http://www.xsjjys.com/data/out/51/WHDQ-511971743.jpg",
                ""
        )
        speaks.add(speak4)
        val speak5 = ModelSpeak(
                4,
                0,
                "http://www.xsjjys.com/data/out/51/WHDQ-511971798.jpg",
                ""
        )
        speaks.add(speak5)
        val speak6 = ModelSpeak(
                5,
                1,
                "http://www.xsjjys.com/data/out/51/WHDQ-511971809.jpg",
                ""
        )
        speaks.add(speak6)
        val speak7 = ModelSpeak(
                6,
                1,
                "http://www.xsjjys.com/data/out/51/WHDQ-511971838.jpg",
                ""
        )
        speaks.add(speak7)
        val speak8 = ModelSpeak(
                7,
                1,
                "http://www.xsjjys.com/data/out/51/WHDQ-511972131.jpg",
                ""
        )
        speaks.add(speak8)
        val speak9 = ModelSpeak(
                8,
                1,
                "http://www.xsjjys.com/data/out/51/WHDQ-511971898.jpg",
                ""
        )
        speaks.add(speak9)
        val speak10 = ModelSpeak(
                9,
                1,
                "http://www.xsjjys.com/data/out/51/WHDQ-511972332.jpg",
                ""
        )
        speaks.add(speak10)
        val speak11 = ModelSpeak(
                10,
                1,
                "http://www.xsjjys.com/data/out/51/WHDQ-511972041.jpg",
                ""
        )
        speaks.add(speak11)
        val speak12 = ModelSpeak(
                11,
                2,
                "http://www.xsjjys.com/data/out/51/WHDQ-511972068.jpg",
                ""
        )
        speaks.add(speak12)
        val speak13 = ModelSpeak(
                12,
                2,
                "http://www.xsjjys.com/data/out/51/WHDQ-511972120.jpg",
                ""
        )
        speaks.add(speak13)
        val speak14 = ModelSpeak(
                13,
                2,
                "http://www.xsjjys.com/data/out/51/WHDQ-511972385.jpg",
                ""
        )
        speaks.add(speak14)
        return speaks
    }
}