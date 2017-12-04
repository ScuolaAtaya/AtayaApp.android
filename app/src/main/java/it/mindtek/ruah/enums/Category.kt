package it.mindtek.ruah.enums

/**
 * Created by alessandrogaboardi on 04/12/2017.
 */
enum class Category(val value: Int) {
    UNDERSTAND(0),
    TALK(1),
    READ(2),
    WRITE(3);

    companion object {
        fun from(value: Int): Category? = Category.values().firstOrNull { it.value == value }
    }
}