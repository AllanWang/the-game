package ca.allanwang.thegame.common.data

fun State.tick(tick: Long): State =
    copy(tick = tick + 1) // TODO sync with input? Or always increment by 1
        .woodTick()

private fun State.computeRates(): State {

    TODO()
}

private fun State.woodTick(): State = copy(wood = wood.tick(0.1f))