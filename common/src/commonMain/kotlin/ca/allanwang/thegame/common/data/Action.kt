package ca.allanwang.thegame.common.data

sealed interface Action {
    data class Tick(val seconds: Long) : Action
}

fun Item.tick(tick: Action.Tick, amount: Float): Item {
    if (!unlocked) return this
    if (amount == 0f) return this
    return copy(
        storage = storage.tick(amount)
    )
}

fun Item.Storage.tick(amount: Float): Item.Storage {
    if (amount == 0f) return this
    return copy(value = (value + amount).bounded(0f..max))
}