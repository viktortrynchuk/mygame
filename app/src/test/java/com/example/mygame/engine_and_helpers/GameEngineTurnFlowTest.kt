package com.example.mygame.engine_and_helpers

import androidx.fragment.app.FragmentActivity
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GameEngineTurnFlowTest {

    private lateinit var activity: FragmentActivity

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(FragmentActivity::class.java).setup().get()
    }

    @Test
    fun `endTurn commits, ticks, ingests, and reports battle alert`() = runBlockingTest {

        val h = buildGameEngineForTest(activity)

        // Simulate a battle alert in inbox
        coEvery { h.post.inbox(h.session.actorId) } returns inbox("BATTLE_AT:${h.session.currentLandId}")

        val res = h.engine.endTurn()

        assertThat(res.newTurn).isEqualTo(2)
        assertThat(res.battleAlertAtMyLand).isTrue()

        // Verify main side-effects called
        coVerify(exactly = 1) { h.commits.commitTurn() }
        coVerify(exactly = 1) { h.turns.tick() }
        coVerify(exactly = 1) { h.ingestion.ingestReportsFor(h.session.actorId) }
        coVerify(exactly = 1) { h.audit.log("END_TURN", any()) }
    }

    @Test
    fun `skipTurnsUntilImportant returns null when nothing important happens`() = runBlockingTest {
        val (engine, session, _, _, _, _, post) = buildGameEngineForTest(
            activity
        )
        coEvery { post.inbox(session.actorId) } returns inbox() // empty

        val res = engine.skipTurnsUntilImportant(count = 3)
        assertThat(res).isNull()
    }

    @Test
    fun `skipTurnsUntilImportant stops when battle alert appears`() = runBlockingTest {
        val (engine, session, _, _, _, _, post) = buildGameEngineForTest(
            activity
        )
        // First two turns no alerts, third turn battle
        coEvery { post.inbox(session.actorId) } returnsMany listOf(
            inbox(),
            inbox(),
            inbox("BATTLE_AT:${session.currentLandId}")
        )

        val res = engine.skipTurnsUntilImportant(5)
        assertThat(res).isNotNull()
        assertThat(res!!.battleAlertAtMyLand).isTrue()
    }
}
