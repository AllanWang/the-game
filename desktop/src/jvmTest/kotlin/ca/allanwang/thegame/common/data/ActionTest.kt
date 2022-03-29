package ca.allanwang.thegame.common.data

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test


class ActionTest {

    @Test
    fun storageTick() {
        val storage = Item.Storage(value = 1f, max = 10f)

        assertThat(storage.tick(2f).value).isEqualTo(3f)
        assertThat(storage.tick(20f).value).isEqualTo(10f)
        assertThat(storage.tick(-0.5f).value).isEqualTo(0.5f)
        assertThat(storage.tick(-5f).value).isEqualTo(0f)
    }
}