package ca.allanwang.thegame.common.data.actions

import ca.allanwang.thegame.common.data.Action
import ca.allanwang.thegame.common.data.Item
import ca.allanwang.thegame.common.data.State
import ca.allanwang.thegame.common.data.update
import com.google.common.flogger.FluentLogger

private val logger = FluentLogger.forEnclosingClass()

fun State.workerUpdate(action: Action.WorkerUpdate): State {
    val afterItem = update(action.key) { workerUpdate(action) } ?: return this
    val newWorkerCount = workers.count - action.delta
    if (newWorkerCount !in (0..workers.maxCount)) return this
    val newWorker = workers.copy(count = newWorkerCount)
    return afterItem.copy(workers = newWorker)
}

private fun Item.workerUpdate(action: Action.WorkerUpdate): Item? {
    val newWorker = workers.workerUpdate(action) ?: return null
    return copy(workers = newWorker)
}

private fun Item.Workers.workerUpdate(action: Action.WorkerUpdate): Item.Workers? {
    val newCount = count + action.delta
    if (newCount < 0) return null
    if (maxCount != null && newCount > maxCount) return null
    return copy(count = newCount)
}