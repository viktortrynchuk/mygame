package com.example.mygame.engine_and_helpers

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.example.mygame.dao.SessionDAO
import com.example.mygame.dao.persistence_and_game_state.ScenarioDao
import com.example.mygame.database.foundations_core.TurnClockEntity
import com.example.mygame.database.messaging_and_information.MessageEntity
import com.example.mygame.database.persistence_and_game_state.CurrentSession
import com.example.mygame.engine_and_helpers.dignity_duels_conflicts.DignityService
import com.example.mygame.engine_and_helpers.dignity_duels_conflicts.DuelOddsCalculator
import com.example.mygame.engine_and_helpers.entertainment_and_social.EntertainmentImpactCalculator
import com.example.mygame.engine_and_helpers.entertainment_and_social.SocialService
import com.example.mygame.engine_and_helpers.foundations_core.AuditLogger
import com.example.mygame.engine_and_helpers.foundations_core.TurnClockService
import com.example.mygame.engine_and_helpers.justice_and_court.JusticeService
import com.example.mygame.engine_and_helpers.messaging_and_information.PostOffice
import com.example.mygame.engine_and_helpers.messaging_and_information.ReportIngestion
import com.example.mygame.engine_and_helpers.persistence_and_game_state.CommitService
import com.example.mygame.engine_and_helpers.ui.DefaultRandom
import com.example.mygame.engine_and_helpers.ui.RandomProvider
import io.mockk.coEvery
import io.mockk.mockk

/** Simple deterministic RNG for tests */
class FixedRandom(private val values: IntArray) : RandomProvider {
    private var i = 0
    override fun nextInt(bound: Int): Int {
        val v = values[i % values.size] % bound
        i++
        return if (v < 0) v + bound else v
    }
}

data class EngineHarness(
    val engine: GameEngine,
    val session: CurrentSession,
    // expose mocks so tests can verify
    val commits: CommitService,
    val turns: TurnClockService,
    val ingestion: ReportIngestion,
    val audit: AuditLogger,
    val post: PostOffice,
)

/** Build a GameEngine with mocks; you can override any dependency if needed. */
fun buildGameEngineForTest(activity: FragmentActivity): EngineHarness {
    // Mocks
    val scenarioDao = mockk<ScenarioDao>(relaxed = true)
    val sessionDao  = mockk<SessionDAO>(relaxed = true)

    val commits     = mockk<CommitService>(relaxed = true)
    val turns       = mockk<TurnClockService>()
    val audit       = mockk<AuditLogger>(relaxed = true)
    val post        = mockk<PostOffice>(relaxed = true)
    val ingestion   = mockk<ReportIngestion>(relaxed = true)
    val dignity     = mockk<DignityService>(relaxed = true)
    val duelOdds    = mockk<DuelOddsCalculator>(relaxed = true)
    val social      = mockk<SocialService>(relaxed = true)
    val entertain   = mockk<EntertainmentImpactCalculator>(relaxed = true)
    val justice     = mockk<JusticeService>(relaxed = true)

    // session used by GameEngine
    val session = CurrentSession(
        scenarioId    = 1L,
        actorId       = 100L,
        currentLandId = 200L,
        startingView  = 1,
        currentTurn   = 1L
    )

    // Default stubs used by turn-flow tests
    coEvery { commits.commitTurn() } returns Unit
    // tick() returns next turn (2)
    coEvery { turns.tick() } returns TurnClockEntity(turn = 2, isNight = false, season = "SPRING", seed = 42)
    coEvery { turns.read() } returns TurnClockEntity(turn = 1, isNight = false, season = "SPRING", seed = 41)
    coEvery { ingestion.ingestReportsFor(any()) } returns 0

    // Build engine with mocks
    val engine = GameEngine(
        ctx = activity as Context,
        scenarioDao = scenarioDao,
        sessionDao = sessionDao,
        turns = turns,
        commits = commits,
        audit = audit,
        post = post,
        ingestion = ingestion,
        dignity = dignity,
        duelOdds = duelOdds,
        social = social,
        entertainImpact = entertain,
        justice = justice,
        session = session,
        rng = DefaultRandom()
    )

    return EngineHarness(engine, session, commits, turns, ingestion, audit, post)
}

/** Convenience inbox builder */
fun inbox(vararg payloads: String, actorId: Long = 101L): List<MessageEntity> =
    payloads.mapIndexed { i, p ->
        MessageEntity(
            id = (i + 1).toLong(),
            fromActorId = 999L,
            toActorId = actorId,
            toRole = null,
            type = "LETTER",
            stampBroken = false,
            sentTurn = 1,
            payload = p
        )
    }
object GameEngineTestTools {
    fun build(
        context: Context = mockk<FragmentActivity>(relaxed = true),
        scenarioDao: ScenarioDao = mockk(relaxed = true),
        sessionDao: SessionDAO = mockk(relaxed = true),
        turns: TurnClockService = mockk(relaxed = true),
        commits: com.example.mygame.engine_and_helpers.persistence_and_game_state.CommitService = mockk(relaxed = true),
        audit: AuditLogger = mockk(relaxed = true),
        post: PostOffice = mockk(relaxed = true),
        ingestion: ReportIngestion = mockk(relaxed = true),
        dignity: DignityService = mockk(relaxed = true),
        duelOdds: DuelOddsCalculator = mockk(relaxed = true),       // <-- pass your mock in
        social: SocialService = mockk(relaxed = true),
        entertainImpact: EntertainmentImpactCalculator = mockk(relaxed = true),
        justice: JusticeService = mockk(relaxed = true),
        session: CurrentSession = CurrentSession(
            scenarioId = 1L, actorId = 100L, currentLandId = 10L, startingView = 1, currentTurn = 0
        ),
        rng: RandomProvider = object : RandomProvider { override fun nextInt(bound: Int) = 0 }
    ): GameEngine {
        return GameEngine(
            ctx = context,
            scenarioDao = scenarioDao,
            sessionDao = sessionDao,
            turns = turns,
            commits = commits,
            audit = audit,
            post = post,
            ingestion = ingestion,
            dignity = dignity,
            duelOdds = duelOdds,            // <-- injected here
            social = social,
            entertainImpact = entertainImpact,
            justice = justice,
            session = session,
            rng = rng
        )
    }
}