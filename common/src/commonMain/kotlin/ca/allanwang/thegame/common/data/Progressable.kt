package ca.allanwang.thegame.common.data

import kotlin.math.min
import kotlin.math.pow

//data class Item(
//    override val value: Float,
//    override val max: Float,
//    override val speed: Float,
//) : Progressable

enum class ResourceName(val id: Int) {
    Wood(id = 0),
    Iron(id = 1)
}

enum class StorageItem(
    val key: String
)

enum class Role(val key: ResourceName, )

enum class ResourceItem(
    val key: ResourceName,
    val rate: UpgradableConstant,
    val storage: UpgradableConstant? = null
) {
    Wood(
        key = ResourceName.Wood,
        rate = UpgradableConstant(
            base = 0.1f,
            upgradeFactor = 0.05f,
            upgradeExponent = 1.1f
        ),
    ),
    Iron(
        key = ResourceName.Iron,
        rate = UpgradableConstant(
            base = 0.1f,
            upgradeFactor = 0.05f,
            upgradeExponent = 1.1f
        ),
    )
    ;

    fun resourceName() = name


}

interface ResourceConstant {
    val name: String

    // Rate per second
    val baseRate: Float
}

interface Entity {
    val name: String
    val production: Upgradable
    val workers: Upgradable
    val storage: Upgradable

    fun maxRate(): Float
}

interface Upgradable : UpgradableConstant {
    val upgrade: Int
    val upgradeMax: Int

    val value: Float
        get() = upgradeFactor * (upgrade.toFloat().pow(upgradeExponent)) + base
}

fun UpgradableConstant(
    base: Float,
    upgradeFactor: Float,
    upgradeExponent: Float
): UpgradableConstant =
    UpgradableConstantImpl(base, upgradeFactor, upgradeExponent)

private data class UpgradableConstantImpl(
    override val base: Float,
    override val upgradeFactor: Float,
    override val upgradeExponent: Float
) : UpgradableConstant

/**
 * Representation of an upgradable feature.
 *
 * Function is upgradeFactor * (upgradeCount ^ upgradeExponent) + base
 */
interface UpgradableConstant {
    val base: Float
    val upgradeFactor: Float
    val upgradeExponent: Float
}

data class Progressable(
    val value: Float,
    val max: Float,
    val speed: Float,
)

fun Progressable.newValue(nanos: Long): Float =
    min(max, value + (speed * nanos / 1_000_000))