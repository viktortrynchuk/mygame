package com.example.mygame.engine_and_helpers

import androidx.fragment.app.FragmentActivity
import com.example.mygame.engine_and_helpers.justice_and_court.JusticeService
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GameEngineJusticeMenuTest {

    private lateinit var activity: FragmentActivity

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(FragmentActivity::class.java).setup().get()
    }

    @Test
    fun `judgeVerdict opens trial and sets verdict`() = runBlockingTest {
        val justice: JusticeService = mockk(relaxed = true)
        coEvery { justice.openTrial(crimeId = 7L, judgeNobleId = any(), startedTurn = any()) } returns 999L

        val (engine, _, _) = buildGameEngineForTest(activity)
        engine.judgeVerdict(crimeId = 7L, guilty = true)

        coVerify { justice.openTrial(7L, any(), any()) }
        coVerify { justice.setVerdict(999L, true, any()) }
    }

    @Test
    fun `startNewGameFromMenu respects confirmDrop`() = runBlockingTest {
        val (engine, session, _) = buildGameEngineForTest(activity)
        session.scenarioId = 10L

        val noGo = engine.startNewGameFromMenu(scenarioId = 5L, confirmDrop = false)
        assertThat(noGo).isFalse()

        val ok = engine.startNewGameFromMenu(scenarioId = 5L, confirmDrop = true)
        assertThat(ok).isTrue()
        assertThat(session.scenarioId).isEqualTo(5L)
    }
}