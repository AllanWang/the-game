package ca.allanwang.thegame.common.data

object Rates {
    private val costs: Map<Key, List<Item.Rate>> = mapOf(
        Key.Wood to listOf(),
        Key.Boards to listOf(Item.Rate(key = Key.Wood, rate = -0.1f)),
    )

    private val rates: Map<Key, Float> = mapOf(
        Key.Wood to 0.1f,
        Key.Boards to 0.1f,
    )

    private val fullRates: Map<Key, List<Item.Rate>>

    init {
        require(costs.keys == rates.keys) { "Costs and rates keys mismatch ${costs.keys} ${rates.keys}" }
        costs.forEach { (k, v) ->
            require(k !in v.map { it.key }
                .toSet()) { "Cyclic dependency for $k" }
            v.forEach { r ->
                require(r.rate < 0) { "Cost is non negative for $k: $r" }
            }
        }
        val fullRates: Map<Key, MutableList<Item.Rate>> =
            rates.mapValues { (k, v) ->
                mutableListOf(
                    Item.Rate(
                        key = k,
                        rate = v
                    )
                )
            }

        costs.forEach { (k, v) ->
            fullRates.getValue(k).addAll(v)
        }

        this.fullRates = fullRates
    }

    fun rates(key: Key): List<Item.Rate> = fullRates.getValue(key)
}