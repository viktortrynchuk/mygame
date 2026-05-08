package com.example.mygame.engine_and_helpers

import androidx.fragment.app.FragmentActivity
import com.example.mygame.engine_and_helpers.dignity_duels_conflicts.DignityService
import com.example.mygame.engine_and_helpers.dignity_duels_conflicts.DuelOddsCalculator
import com.example.mygame.engine_and_helpers.ui.RandomProvider
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import kotlinx.coroutines.test.runTest

@RunWith(RobolectricTestRunner::class)
class GameEngineDuelAndChanceTest {

    private lateinit var activity: FragmentActivity

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(FragmentActivity::class.java).setup().get()
    }


    @Test
    fun `duelStart triggers WIN_A and finalizeDuel`() = runTest {
        // Arrange: create mocks
        val dignity = mockk<DignityService>(relaxed = true)
        val duelOdds = mockk<DuelOddsCalculator>()

        // Stub the mock directly (not engine.duelOdds)
        every { duelOdds.winChance(any(), any()) } returns 100

        // Build SUT with those mocks
        val engine = GameEngineTestTools.build(
            dignity = dignity,
            duelOdds = duelOdds,                           // pass the mock
            rng = object : RandomProvider { override fun nextInt(bound: Int) = 0 } // guarantees WIN_A
        )

        // Act
        val outcome = engine.duelStart(duelId = 1L, opponentId = 2L)

        // Assert
        assertThat(outcome.code).isEqualTo("WIN_A")
        assertThat(outcome.playerDefeated).isFalse()
        coVerify(exactly = 1) { dignity.finalizeDuel(any(), "WIN_A") }
    }

    @Test
    fun `duelTryToAvoid success when rng below threshold`() = runBlockingTest {
        val (engine, _, _) = buildGameEngineForTest(
            activity = activity
        )
        val success = engine.duelTryToAvoid(duelId = 10L, opponentId = 33L)
        assertThat(success).isTrue()
    }

    @Test
    fun `mealStart returns poisoned based on rng`() = runBlockingTest {
        // rng=1 -> <5% so poisoned
        val (engine, _, _) = buildGameEngineForTest(activity)
        val poisoned = engine.mealStart(listOf("beer", "meat"))
        assertThat(poisoned).isTrue()
    }
}