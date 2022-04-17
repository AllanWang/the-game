package ca.allanwang.thegame.common.data

import com.google.common.flogger.FluentLogger
import kotlin.math.max
import kotlin.math.min

data class State(
    val tick: Long = 0L,
    val wood: Item = Key.Wood.defaultItem().copy(unlocked = true),
    val boards: Item = Key.Boards.defaultItem().copy(unlocked = true),
)

data class Item(
    val constants: Constants,
    val unlocked: Boolean,
    val count: Count,
    val storage: Storage,
    val transient: Transient,
) {
    data class Constants(
        val key: String,
        val name: String,
    )

    data class Count(
        val count: Int,
        val maxCount: Int?,
        val workers: Int,
    )

    data class Storage(
        val value: Float,
        val max: Float,
    )

    data class Rate(
        val key: Key,
        val rate: Float,
    )

    data class Transient(
        val rates: List<Rate>
    )
}

enum class Key(val key: String, val title: String) {
    Wood(key = "wood", title = "Wood"),
    Boards(key = "boards", title = "Boards"),
}

private val logger: FluentLogger = FluentLogger.forEnclosingClass()

fun Key.defaultItem(): Item = Item(
    constants = constants(),
    unlocked = false,
    count = defaultCount(),
    storage = defaultStorage(),
    transient = defaultTransient(),
)

fun Key.constants(): Item.Constants =
    Item.Constants(key = key, name = title)

val Item.Storage.maxRate: Float
    get() = (max - value) / max

fun Key.defaultCount(): Item.Count = Item.Count(
    count = 0,
    maxCount = defaultMaxCount(),
    workers = 0,
)

fun Key.defaultMaxCount(): Int? = when (this) {
    Key.Wood -> null
    Key.Boards -> 10
}

fun Key.defaultStorage(): Item.Storage = Item.Storage(
    value = 0f,
    max = defaultMaxStorage(),
)

fun Key.defaultMaxStorage(): Float = when (this) {
    Key.Wood -> 10f
    Key.Boards -> 10f
}

fun Key.defaultTransient(): Item.Transient = Item.Transient(
    rates = Rates.rates(this)
)

fun State.update(key: Key, action: Item.() -> Item?): State {
    fun update(item: Item, copier: (Item) -> State): State {
        val newItem = item.action() ?: return this
        return copier(newItem)
    }
    return when (key) {
        Key.Wood -> update(wood) { copy(wood = it) }
        else -> TODO()
    }
}
