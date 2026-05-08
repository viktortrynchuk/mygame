package com.example.mygame.engine_and_helpers

import androidx.fragment.app.FragmentActivity
import com.google.common.truth.Truth.assertThat
import com.example.mygame.database.messaging_and_information.DeliveryMethod
import com.example.mygame.database.messaging_and_information.MessageType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GameEngineOrdersTest {

    private lateinit var activity: FragmentActivity

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(FragmentActivity::class.java).setup().get()
    }

    @Test
    fun `requestLandReport sends a sealed letter to Chancellor`() = runTest {
        val (engine, _, _, _, _, _, post) = buildGameEngineForTest(activity)

        val toRoleSlot = slot<String?>()
        val messageTypeSlot = slot<MessageType>()
        val deliverySlot = slot<DeliveryMethod>()
        val bodySlot = slot<String>()
        val sealedSlot = slot<Boolean>()

        coEvery {
            post.send(
                from = any(),
                toActorId = any(),
                toRole = captureNullable(toRoleSlot),
                messageType = capture(messageTypeSlot),
                delivery = capture(deliverySlot),
                body = capture(bodySlot),
                turn = any(),
                sealed = capture(sealedSlot)
            )
        } returns 1L

        engine.requestLandReport(
            landId = 301L,
            via = DeliveryMethod.MESSENGER
        )

        coVerify(exactly = 1) {
            post.send(any(), any(), any(), any(), any(), any(), any(), any())
        }

        assertThat(toRoleSlot.captured).isEqualTo("CHANCELLOR")
        assertThat(messageTypeSlot.captured).isEqualTo(MessageType.LETTER)
        assertThat(deliverySlot.captured).isEqualTo(DeliveryMethod.MESSENGER)
        assertThat(bodySlot.captured).isEqualTo("REQUEST:LAND_REPORT:301")
        assertThat(sealedSlot.captured).isTrue()
    }

    @Test
    fun `movePlayerTo updates session land and sends escort order`() = runTest {
        val (engine, session, _, _, _, _, post) = buildGameEngineForTest(activity)

        var sent = false

        coEvery {
            post.send(any(), any(), any(), any(), any(), any(), any(), any())
        } answers {
            sent = true
            2L
        }

        engine.movePlayerTo(landId = 404L)

        assertThat(session.currentLandId).isEqualTo(404L)
        assertThat(sent).isTrue()

        coVerify(exactly = 1) {
            post.send(
                from = any(),
                toActorId = any(),
                toRole = eq("PLAYER"),
                messageType = eq(MessageType.ORAL),
                delivery = eq(DeliveryMethod.LOCAL),
                body = eq("ORDER_KIND=MOVE_PLAYER_TO|TARGET_LAND=404"),
                turn = any(),
                sealed = eq(false)
            )
        }
    }

    @Test
    fun `battleMapMoveUnit sends move order`() = runTest {
        val (engine, _, _, _, _, _, post) = buildGameEngineForTest(activity)

        var seen = false

        coEvery {
            post.send(
                from = any(),
                toActorId = any(),
                toRole = eq("UNIT_77"),
                messageType = eq(MessageType.ORAL),
                delivery = eq(DeliveryMethod.LOCAL),
                body = eq("ORDER_KIND=MOVE_BATTLE_UNIT|ARMIES=77|NOTE=MOVE_TO:5,6"),
                turn = any(),
                sealed = eq(false)
            )
        } answers {
            seen = true
            3L
        }

        engine.battleMapMoveUnit(
            unitId = 77,
            toX = 5,
            toY = 6,
            via = DeliveryMethod.LOCAL
        )

        assertThat(seen).isTrue()
    }
}