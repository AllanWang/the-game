package ca.allanwang.thegame.common.data

sealed interface Action {
    object Reset : Action
    data class Tick(val seconds: Long) : Action
    data class WorkerUpdate(val key: Key, val delta: Int): Action
}

fun Item.tick(amount: Float): Item {
    if (!unlocked) return this
    if (amount == 0f) return this
    return copy(
        storage = storage.tick(amount)
    )
}

fun Item.Storage.tick(amount: Float): Item.Storage {
    if (amount == 0f) return this
    return copy(value = (value + amount).coerceIn(0f..max))
}