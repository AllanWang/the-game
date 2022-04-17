package ca.allanwang.thegame.common.data

import com.google.common.flogger.FluentLogger

data class State(
    val tick: Long = 0L,
    val wood: Item = Key.Wood.defaultItem().copy(unlocked = true),
    val boards: Item = Key.Boards.defaultItem().copy(unlocked = true),
    val workers: Workers = Workers(count = 10, maxCount = 10)
)

data class Workers(
    val count: Int,
    val maxCount: Int
)

data class Item(
    val constants: Constants,
    val unlocked: Boolean,
    val workers: Workers,
    val storage: Storage,
    val transient: Transient,
) {
    data class Constants(
        val key: Key,
        val name: String,
    )

    data class Workers(
        val count: Int,
        val maxCount: Int?,
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
    workers = defaultCount(),
    storage = defaultStorage(),
    transient = defaultTransient(),
)

fun Key.constants(): Item.Constants =
    Item.Constants(key = this, name = title)

val Item.Storage.maxRate: Float
    get() = (max - value) / max

fun Key.defaultCount(): Item.Workers = Item.Workers(
    count = 0,
    maxCount = defaultMaxCount(),
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

fun State.item(key: Key): Item = when (key) {
    Key.Wood -> wood
    Key.Boards -> boards
}

/**
 * Updates a specified item based on [key].
 *
 * If no changes occurs, returns null.
 */
fun State.update(key: Key, action: Item.() -> Item?): State? {
    fun update(item: Item, copier: (Item) -> State): State? {
        val newItem = item.action() ?: return null
        return copier(newItem)
    }
    return when (key) {
        Key.Wood -> update(wood) { copy(wood = it) }
        Key.Boards -> update(boards) { copy(boards = it) }
    }
}
