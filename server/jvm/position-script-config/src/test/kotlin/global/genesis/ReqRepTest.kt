package global.genesis

import global.genesis.commons.model.GenesisSet.Companion.genesisSet
import global.genesis.commons.standards.MessageType
import global.genesis.db.DbRecord
import global.genesis.gen.dao.Trade
import global.genesis.gen.dao.enums.Side
import global.genesis.message.core.request.Reply
import global.genesis.message.core.request.Request
import global.genesis.message.core.workflow.message.RequestReplyWorkflow
import global.genesis.message.core.workflow.message.requestReplyWorkflowBuilder
import global.genesis.testsupport.AbstractGenesisTestSupport
import global.genesis.testsupport.GenesisTestConfig
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ReqRepTest : AbstractGenesisTestSupport<Reply<*>>(
    GenesisTestConfig {
        packageName = "global.genesis.requestreply.pal"
        genesisHome = "/GenesisHome/"
        scriptFileName = "position-reqrep.kts"
        initialDataFile = "data/TEST_DATA.csv"
        addAuthCacheOverride("ENTITY_VISIBILITY")
    }
) {
    override fun systemDefinition(): Map<String, Any> = mapOf("IS_SCRIPT" to "true")

    object TradeFlow : RequestReplyWorkflow<Trade.ById, Trade> by requestReplyWorkflowBuilder()

    @Before
    fun setUp(): Unit = runBlocking {
        authorise("ENTITY_VISIBILITY", "1", "JohnDoe")
        authorise("ENTITY_VISIBILITY", "1", "JaneDoe")
        authorise("ENTITY_VISIBILITY", "2", "JaneDoe")

        val trader = DbRecord.dbRecord("RIGHT_SUMMARY") {
            "USER_NAME" with "JohnDoe"
            "RIGHT_CODE" with "TRADER"
        }
        val support = DbRecord.dbRecord("RIGHT_SUMMARY") {
            "USER_NAME" with "JaneDoe"
            "RIGHT_CODE" with "SUPPORT"
        }
        rxDb.insert(trader).blockingGet()
        rxDb.insert(support).blockingGet()

        entityDb.insert(Trade {
            tradeId = "1"
            counterpartyId = "1"
            instrumentId = "2"
            side = Side.BUY
            price = 1.0
            quantity = 1
        })
        entityDb.insert(Trade {
            tradeId = "2"
            counterpartyId = "2"
            instrumentId = "2"
            side = Side.SELL
            price = 2.0
            quantity = 2
        })
    }

    @Test
    fun `test get trade as trader`(): Unit = runBlocking {
        val request = Request(Trade.ById("1"), "REQ_TRADE").apply { userName = "JohnDoe" }
        val reply: List<Trade> = sendRequest(TradeFlow, request)
        assertEquals(1, reply.size)
    }

    @Test
    fun `test get trade as support`(): Unit = runBlocking {
        val request = genesisSet {
            MessageType.MESSAGE_TYPE with "REQ_TRADE"
            "USER_NAME" with "JaneDoe"
        }
        val result = messageClient.sendReqRep(request).get()
        assertEquals(2, result?.getArray<Any>("REPLY")?.size)
    }
}
