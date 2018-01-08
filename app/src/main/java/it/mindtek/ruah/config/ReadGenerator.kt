package it.mindtek.ruah.config

import it.mindtek.ruah.db.models.ModelRead
import it.mindtek.ruah.db.models.ModelReadAnswer

/**
 * Created by alessandrogaboardi on 21/12/2017.
 */
object ReadGenerator {
    fun getRead(): MutableList<ModelRead>{
        val reads = mutableListOf<ModelRead>()
        val read1 = ModelRead(
                0,
                0,
                "https://image.freepik.com/free-photo/cute-cat-picture_1122-449.jpg"
        )
        reads.add(read1)
        val read2 = ModelRead(
                1,
                0,
                "https://ichef-1.bbci.co.uk/news/976/media/images/83351000/jpg/_83351965_explorer273lincolnshirewoldssouthpicturebynicholassilkstone.jpg")
        reads.add(read2)
        return reads
    }

    fun getAnswers(): MutableList<ModelReadAnswer>{
        val readAnswers: MutableList<ModelReadAnswer> = mutableListOf()
        val readAnswer1 = ModelReadAnswer(
                0,
                0,
                "Answer1",
                "Audio",
                true
        )
        readAnswers.add(readAnswer1)
        val readAnswer2 = ModelReadAnswer(
                1,
                0,
                "Answer2",
                "Audio",
                false
        )
        readAnswers.add(readAnswer2)
        val readAnswer3 = ModelReadAnswer(
                2,
                0,
                "Answer3",
                "Audio",
                true
        )
        readAnswers.add(readAnswer3)
        val readAnswer4 = ModelReadAnswer(
                3,
                0,
                "Answer4",
                "Audio",
                true
        )
        readAnswers.add(readAnswer4)
        val readAnswer5 = ModelReadAnswer(
                4,
                0,
                "Answer5",
                "Audio",
                false
        )
        readAnswers.add(readAnswer5)
        val readAnswer6 = ModelReadAnswer(
                5,
                0,
                "Answer6",
                "Audio",
                false
        )
        readAnswers.add(readAnswer6)
        val readAnswer7 = ModelReadAnswer(
                6,
                1,
                "Answer7",
                "Audio",
                false
        )
        readAnswers.add(readAnswer7)
        val readAnswer8 = ModelReadAnswer(
                7,
                1,
                "Answer8",
                "Audio",
                false
        )
        readAnswers.add(readAnswer8)
        val readAnswer9 = ModelReadAnswer(
                8,
                1,
                "Answer9",
                "Audio",
                true
        )
        readAnswers.add(readAnswer9)
        val readAnswer10 = ModelReadAnswer(
                9,
                1,
                "Answer10",
                "Audio",
                false
        )
        readAnswers.add(readAnswer10)
        val readAnswer11 = ModelReadAnswer(
                10,
                1,
                "Answer11",
                "Audio",
                true
        )
        readAnswers.add(readAnswer11)
        val readAnswer12 = ModelReadAnswer(
                11,
                1,
                "Answer12",
                "Audio",
                false
        )
        readAnswers.add(readAnswer12)
        return readAnswers
    }
}