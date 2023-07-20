package global.genesis

import global.genesis.commons.model.GenesisSet
import global.genesis.db.DbRecord
import global.genesis.gen.dao.InstrumentPriceSubscription
import global.genesis.gen.dao.Trade
import global.genesis.gen.dao.TradeAudit
import global.genesis.gen.dao.enums.Side
import global.genesis.gen.dao.enums.TradeStatus
import global.genesis.message.core.error.StandardError
import global.genesis.message.core.event.Event
import global.genesis.message.core.event.EventReply
import global.genesis.position.message.event.TradeAllocated
import global.genesis.position.message.event.TradeCancel
import global.genesis.position.message.event.TradeInsert
import global.genesis.position.message.event.TradeModify
import global.genesis.testsupport.AbstractGenesisTestSupport
import global.genesis.testsupport.GenesisTestConfig
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TradingEventHandlerTest : AbstractGenesisTestSupport<GenesisSet>(
  GenesisTestConfig {
    packageName = "global.genesis.eventhandler.pal"
    genesisHome = "/GenesisHome/"
    scriptFileName = "position-eventhandler.kts"
    parser = { it }
    initialDataFile = "data/TEST_DATA.csv"
    addAuthCacheOverride("ENTITY_VISIBILITY")
  }
) {
  override fun systemDefinition(): Map<String, Any> = mapOf("IS_SCRIPT" to "true")

  @Before
  fun setUp() {
    authorise("ENTITY_VISIBILITY", "1", "JohnDoe")
    authorise("ENTITY_VISIBILITY", "1", "TestUser")

    val trader = DbRecord.dbRecord("RIGHT_SUMMARY") {
      "USER_NAME" with "JohnDoe"
      "RIGHT_CODE" with "TRADER"
    }
    val support = DbRecord.dbRecord("RIGHT_SUMMARY") {
      "USER_NAME" with "JaneDoe"
      "RIGHT_CODE" with "SUPPORT"
    }
    val testUser = DbRecord.dbRecord("RIGHT_SUMMARY") {
      "USER_NAME" with "TestUser"
      "RIGHT_CODE" with "TRADER"
    }
    rxDb.insertAll(listOf(trader, support, testUser)).blockingGet()
  }

  @Test
  fun `test insert trade`(): Unit = runBlocking {
    val message = Event(
      details = TradeInsert(
        counterpartyId = "1",
        instrumentId = "2",
        side = Side.BUY,
        price = 1.123,
        quantity = 1000,
        enteredBy = "JohnDoe",
        tradeDatetime = DateTime.now().toString(ISODateTimeFormat.dateHourMinuteSecond())
      ),
      messageType = "EVENT_TRADE_INSERT",
      userName = "JohnDoe"
    )

    val result: EventReply? = messageClient.suspendRequest(message)

    result.assertedCast<EventReply.EventAck>()
    val trades = entityDb.getBulk<Trade>().toList()
    val trade = trades[0]
    assertNotNull(trade)
    assertEquals(1, trades.size)
    assertEquals("1", trade.counterpartyId)
    assertEquals("2", trade.instrumentId)
    assertEquals(TradeStatus.NEW, trade.tradeStatus)
    assertEquals(Side.BUY, trade.side)
    assertEquals(1.123, trade.price)
    assertEquals(1000, trade.quantity)
  }

  @Test
  fun `test modify trade`(): Unit = runBlocking {
    val trade = Trade {
      counterpartyId = "1"
      instrumentId = "2"
      side = Side.BUY
      price = 1.123
      quantity = 1000
      enteredBy = "JohnDoe"
      tradeDatetime = DateTime.now()
    }
    entityDb.insert(trade)
    val insertedTradeId = entityDb.getBulk<Trade>().toList()[0].tradeId

    val modifyMessage = Event(
      details = TradeModify(
        tradeId = insertedTradeId,
        counterpartyId = "1",
        instrumentId = "2",
        side = Side.SELL,
        price = 5.123,
        quantity = 2000,
        enteredBy = "JohnDoe",
        tradeDatetime = DateTime.now().toString(ISODateTimeFormat.dateHourMinuteSecond()),
        tradeStatus = TradeStatus.NEW
      ),
      messageType = "EVENT_TRADE_MODIFY",
      userName = "JohnDoe"
    )
    val result: EventReply? = messageClient.suspendRequest(modifyMessage)
    result.assertedCast<EventReply.EventAck>()
    val modifiedTrade = entityDb.get(Trade.byId(insertedTradeId))
    assertEquals(Side.SELL, modifiedTrade?.side)
    assertEquals(5.123, modifiedTrade?.price)
    assertEquals(2000, modifiedTrade?.quantity)
  }

  @Test
  fun `test invalid instrument`(): Unit = runBlocking {
    val message = Event(
      details = TradeInsert(
        counterpartyId = "1",
        instrumentId = "FAKE_INSTRUMENT_ID",
        side = Side.BUY,
        price = 1.123,
        quantity = 1000,
        enteredBy = "JohnDoe",
        tradeDatetime = DateTime.now().toString(ISODateTimeFormat.dateHourMinuteSecond())
      ),
      messageType = "EVENT_TRADE_INSERT",
      userName = "JohnDoe"
    )

    val result: EventReply? = messageClient.suspendRequest(message)

    val eventNack: EventReply.EventNack = result.assertedCast()
    val trades = entityDb.getBulk<Trade>().toList()
    assertEquals(0, trades.size)
    assertThat(eventNack.error).containsExactly(
      StandardError(
        "INTERNAL_ERROR",
        "INSTRUMENT ById(instrumentId=FAKE_INSTRUMENT_ID) not found in database"
      )
    )
  }

  @Test
  fun `test invalid counterparty and instrument`(): Unit = runBlocking {
    val message = Event(
      details = Trade {
        counterpartyId = "FAKE_INSTRUMENT_ID"
        instrumentId = "FAKE_INSTRUMENT_ID"
        side = Side.BUY
        price = 1.123
        quantity = 1000
        enteredBy = "JohnDoe"
        tradeDatetime = DateTime.now()
      },
      messageType = "EVENT_TRADE_INSERT",
      userName = "JohnDoe"
    )

    val result: EventReply? = messageClient.suspendRequest(message)

    result.assertedCast<EventReply.EventNack>()
    val trades = entityDb.getBulk<Trade>().toList()
    assertEquals(0, trades.size)
  }

  @Test
  fun `test cancel trade`(): Unit = runBlocking {
    val trade = Trade {
      counterpartyId = "1"
      instrumentId = "2"
      side = Side.BUY
      price = 1.123
      quantity = 1000
      enteredBy = "JohnDoe"
      tradeDatetime = DateTime.now()
    }
    val tradeId = entityDb.insert(trade).record.tradeId
    val message = Event(
      details = TradeCancel(tradeId),
      messageType = "EVENT_TRADE_CANCEL"
    )

    val result: EventReply? = messageClient.suspendRequest(message)

    result.assertedCast<EventReply.EventAck>()
    assertEquals(TradeStatus.CANCELLED, entityDb.get(Trade.byId(tradeId))?.tradeStatus)
  }

  @Test
  fun `test trying to cancel an already cancelled trade`(): Unit = runBlocking {
    val trade = Trade {
      counterpartyId = "1"
      instrumentId = "2"
      side = Side.BUY
      price = 1.123
      quantity = 1000
      enteredBy = "JohnDoe"
      tradeDatetime = DateTime.now()
      tradeStatus = TradeStatus.CANCELLED
    }
    val tradeId = entityDb.insert(trade).record.tradeId
    val message = Event(
      details = TradeCancel(tradeId),
      messageType = "EVENT_TRADE_CANCEL"
    )

    val result: EventReply? = messageClient.suspendRequest(message)

    val eventNack = result.assertedCast<EventReply.EventNack>()
    assertThat(eventNack.error).containsExactly(
      StandardError(
        "INTERNAL_ERROR",
        "State CANCELLED is immutable"
      )
    )
  }

  @Test
  fun `test allocated trade`(): Unit = runBlocking {
    val trade = Trade {
      counterpartyId = "1"
      instrumentId = "2"
      side = Side.BUY
      price = 1.123
      quantity = 1000
      enteredBy = "JohnDoe"
      tradeDatetime = DateTime.now()
    }
    entityDb.insert(trade)
    val tradeId = entityDb.getBulk<Trade>().toList()[0].tradeId
    val message = Event(
      details = TradeAllocated(tradeId),
      messageType = "EVENT_TRADE_ALLOCATED"
    )

    val result: EventReply? = messageClient.suspendRequest(message)

    result.assertedCast<EventReply.EventAck>()
    assertEquals(TradeStatus.ALLOCATED, entityDb.get(Trade.byId(tradeId))?.tradeStatus)
  }

  @Test
  fun `test trying to allocate a cancelled trade`(): Unit = runBlocking {
    val trade = Trade {
      counterpartyId = "1"
      instrumentId = "2"
      side = Side.BUY
      price = 1.123
      quantity = 1000
      enteredBy = "JohnDoe"
      tradeDatetime = DateTime.now()
      tradeStatus = TradeStatus.CANCELLED
    }
    val tradeId = entityDb.insert(trade).record.tradeId
    val message = Event(
      details = TradeAllocated(tradeId),
      messageType = "EVENT_TRADE_ALLOCATED"
    )

    val result: EventReply? = messageClient.suspendRequest(message)

    val eventNack = result.assertedCast<EventReply.EventNack>()
    assertEquals(TradeStatus.CANCELLED, entityDb.get(Trade.byId(tradeId))?.tradeStatus)
    assertThat(eventNack.error).containsExactly(
      StandardError(
        "INTERNAL_ERROR",
        "Illegal transition: cannot transition from CANCELLED to ALLOCATED"
      )
    )
  }

  @Test
  fun `test trade by TestUser`(): Unit = runBlocking {
    val message = Event(
      details = TradeInsert(
        counterpartyId = "1",
        instrumentId = "2",
        side = Side.BUY,
        price = 1.123,
        quantity = 1000,
        enteredBy = "JohnDoe",
        tradeDatetime = DateTime.now().toString(ISODateTimeFormat.dateHourMinuteSecond())
      ),
      messageType = "EVENT_TRADE_INSERT",
      userName = "TestUser"
    )

    val result: EventReply? = messageClient.suspendRequest(message)

    result.assertedCast<EventReply.EventAck>()
    val trades = entityDb.getBulk<Trade>().toList()
    assertEquals(1, trades.size)
    assertEquals(TradeStatus.NEW, trades[0].tradeStatus)
  }

  @Test
  fun `test allocated trade to cancelled`(): Unit = runBlocking {
    val trade = Trade {
      counterpartyId = "1"
      instrumentId = "2"
      side = Side.BUY
      price = 1.123
      quantity = 1000
      enteredBy = "JohnDoe"
      tradeDatetime = DateTime.now()
      tradeStatus = TradeStatus.ALLOCATED
    }
    val tradeId = entityDb.insert(trade).record.tradeId
    val message = Event(
      details = TradeCancel(tradeId),
      messageType = "EVENT_TRADE_CANCEL"
    )

    val result: EventReply? = messageClient.suspendRequest(message)
    result.assertedCast<EventReply.EventAck>()
    assertEquals(TradeStatus.CANCELLED, entityDb.get(Trade.byId(tradeId))?.tradeStatus)
  }

  @Test
  fun `test trade insert without permission`(): Unit = runBlocking {
    val message = Event(
      details = TradeInsert(
        counterpartyId = "1",
        instrumentId = "2",
        side = Side.BUY,
        price = 1.123,
        quantity = 1000,
        enteredBy = "JaneDoe",
        tradeDatetime = DateTime.now().toString(ISODateTimeFormat.dateHourMinuteSecond())
      ),
      messageType = "EVENT_TRADE_INSERT",
      userName = "JaneDoe"
    )

    val result: EventReply? = messageClient.suspendRequest(message)

    val eventNack = result.assertedCast<EventReply.EventNack>()
    assertEquals(0, entityDb.getBulk<Trade>().toList().size)
    assertThat(eventNack.error).containsExactly(
      StandardError(
        "NOT_AUTHORISED",
        "User JaneDoe lacks sufficient permissions"
      )
    )
  }

  @Test
  fun `test audit table`(): Unit = runBlocking {
    val insertMessage = Event(
      details = TradeInsert(
        counterpartyId = "1",
        instrumentId = "2",
        side = Side.BUY,
        price = 1.123,
        quantity = 1000,
        enteredBy = "JohnDoe",
        tradeDatetime = DateTime.now().toString(ISODateTimeFormat.dateHourMinuteSecond())
      ),
      messageType = "EVENT_TRADE_INSERT",
      userName = "JohnDoe"
    )
    val insertResult: EventReply? = messageClient.suspendRequest(insertMessage)
    insertResult.assertedCast<EventReply.EventAck>()
    val insertedTradeId = entityDb.getBulk<Trade>().toList()[0].tradeId

    val modifyMessage = Event(
      details = TradeModify(
        tradeId = insertedTradeId,
        counterpartyId = "1",
        instrumentId = "2",
        side = Side.BUY,
        price = 10.65,
        quantity = 2000,
        enteredBy = "JohnDoe",
        tradeDatetime = DateTime.now().toString(ISODateTimeFormat.dateHourMinuteSecond()),
        tradeStatus = TradeStatus.NEW
      ),
      messageType = "EVENT_TRADE_MODIFY",
      userName = "JohnDoe"
    )
    val allocateMessage = Event(
      details = TradeAllocated(insertedTradeId),
      messageType = "EVENT_TRADE_ALLOCATED"
    )
    val cancelMessage = Event(
      details = TradeCancel(insertedTradeId),
      messageType = "EVENT_TRADE_CANCEL"
    )

    val modifyResult: EventReply? = messageClient.suspendRequest(modifyMessage)
    modifyResult.assertedCast<EventReply.EventAck>()

    val allocateResult: EventReply? = messageClient.suspendRequest(allocateMessage)
    allocateResult.assertedCast<EventReply.EventAck>()

    val cancelResult: EventReply? = messageClient.suspendRequest(cancelMessage)
    cancelResult.assertedCast<EventReply.EventAck>()

    val tradeAudit: List<TradeAudit> = entityDb.getBulk<TradeAudit>().toList()
    assertEquals(4, tradeAudit.size)
    assertThat(tradeAudit).extracting<String> { it.auditEventType }
      .containsExactlyInAnyOrder(
        "EVENT_TRADE_INSERT",
        "EVENT_TRADE_ALLOCATED",
        "EVENT_TRADE_MODIFY",
        "EVENT_TRADE_CANCEL"
      )
  }

  @Test
  fun `market data is subscribed to on trade entry`(): Unit = runBlocking {
    val message = Event(
      details = TradeInsert(
        counterpartyId = "1",
        instrumentId = "2",
        side = Side.BUY,
        price = 1.123,
        quantity = 1000,
        enteredBy = "JohnDoe",
        tradeDatetime = DateTime.now().toString(ISODateTimeFormat.dateHourMinuteSecond())
      ),
      messageType = "EVENT_TRADE_INSERT",
      userName = "JohnDoe"
    )

    val insertResult: EventReply? = messageClient.suspendRequest(message)
    insertResult.assertedCast<EventReply.EventAck>()

    val priceSubscription: InstrumentPriceSubscription? =
      entityDb.get(InstrumentPriceSubscription.ByInstrumentCode("2", "LSE"))
    assertEquals("2", priceSubscription!!.instrumentCode)
    assertEquals("LSE", priceSubscription.primaryExchangeId)
  }
}
