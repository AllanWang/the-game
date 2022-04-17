package ca.allanwang.thegame.common.data.actions

import ca.allanwang.thegame.common.data.Action
import ca.allanwang.thegame.common.data.Item
import ca.allanwang.thegame.common.data.State


fun State.tick(action: Action.Tick): State =
    copy(tick = action.tick + 1) // TODO sync with input? Or always increment by 1
        .woodTick()

private fun State.computeRates(): State {

    TODO()
}

private fun State.woodTick(): State = copy(wood = wood.tick(0.1f))

private fun Item.tick(amount: Float): Item {
    if (!unlocked) return this
    if (amount == 0f) return this
    return copy(
        storage = storage.tick(amount)
    )
}

private fun Item.Storage.tick(amount: Float): Item.Storage {
    if (amount == 0f) return this
    return copy(value = (value + amount).coerceIn(0f..max))
}