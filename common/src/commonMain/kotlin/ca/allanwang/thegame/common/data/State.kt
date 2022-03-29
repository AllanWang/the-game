package ca.allanwang.thegame.common.data

import kotlin.math.max
import kotlin.math.min

data class State(
    val wood: Item = Key.Wood.defaultItem().copy(unlocked = true),
)

data class Item(
    val constants: Constants,
    val unlocked: Boolean,
    val count: Count,
    val storage: Storage,
) {
    data class Constants(
        val key: String,
        val name: String,
        val rate: Float
    )

    data class Count(
        val count: Int,
        val maxCount: Int?,
        val workers: Int,
    )

    data class Storage(
        val value: Float,
        val max: Float
    )
}

enum class Key(val key: String, val title: String) {
    Wood(key = "wood", title = "Wood")
}

fun Key.defaultItem(): Item = Item(
    constants = constants(),
    unlocked = false,
    count = defaultCount(),
    storage = defaultStorage(),
)

fun Key.constants(): Item.Constants =
    Item.Constants(key = key, name = title, rate = rate())

fun Key.rate() = when (this) {
    Key.Wood -> 0.1f
}

val Item.isolatedRate: Float
    get() = when {
        !unlocked -> 0f
        else -> min(storage.maxRate, count.workers * constants.rate)
    }

val Item.Storage.maxRate: Float
    get() = (max - value) / max

fun Key.defaultCount(): Item.Count = Item.Count(
    count = 0,
    maxCount = defaultMaxCount(),
    workers = 0,
)

fun Key.defaultMaxCount(): Int? = when (this) {
    Key.Wood -> null
}

fun Key.defaultStorage(): Item.Storage = Item.Storage(
    value = 0f,
    max = defaultMaxStorage(),
)

fun Key.defaultMaxStorage(): Float = when (this) {
    Key.Wood -> 100f
}

fun Float.bounded(range: ClosedFloatingPointRange<Float>) =
    min(range.endInclusive, max(range.start, this))