package global.genesis

import global.genesis.commons.model.GenesisSet
import global.genesis.gen.dao.ChartsData
import global.genesis.gen.dao.Position
import global.genesis.gen.dao.Trade
import global.genesis.gen.dao.enums.Side
import global.genesis.gen.dao.enums.TradeStatus
import global.genesis.testsupport.AbstractGenesisTestSupport
import global.genesis.testsupport.GenesisTestConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class ConsolidatorTest : AbstractGenesisTestSupport<GenesisSet>(
  GenesisTestConfig {
    addPackageName("global.genesis.pal.consolidator")
    genesisHome = "/GenesisHome/"
    scriptFileName = "position-consolidator.kts"
    parser = { it }
  }) {

  override fun systemDefinition(): Map<String, Any> = mapOf("IS_SCRIPT" to "true")

  @Test
  fun `position is consolidated when trades are inserted with a trade status of NEW`() = runBlocking {
    val testCase = TestCase(
      numberOfBuys = 5L,
      numberOfSells = 2L,
      tradeQuantity = 5L,
      tradePrice = 100.00,
      tradeStatus = TradeStatus.NEW,
      instrumentId = "1"
    )
    insertTrades(testCase)
    delay(3000)

    val actualPosition = entityDb.getBulk<Position>().toList()[0]

    assertEquals("1", actualPosition.instrumentId)
    assertEquals(15, actualPosition.quantity)
    assertEquals(1500.0, actualPosition.notional)
    assertEquals(1500.0, actualPosition.value)
    assertEquals(25, actualPosition.buyQuantity)
    assertEquals(10, actualPosition.sellQuantity)
  }

  @Test
  fun `position is consolidated when trades are inserted with a trade status of ALLOCATED`() = runBlocking {
    val testCase = TestCase(
      numberOfBuys = 5L,
      numberOfSells = 2L,
      tradeQuantity = 5L,
      tradePrice = 100.00,
      tradeStatus = TradeStatus.ALLOCATED,
      instrumentId = "1"
    )
    insertTrades(testCase)
    delay(3000)

    val actualPosition = entityDb.getBulk<Position>().toList()[0]

    assertEquals("1", actualPosition.instrumentId)
    assertEquals(15, actualPosition.quantity)
    assertEquals(1500.0, actualPosition.notional)
    assertEquals(1500.0, actualPosition.value)
    assertEquals(25, actualPosition.buyQuantity)
    assertEquals(10, actualPosition.sellQuantity)
  }

  @Test
  fun `position is consolidated when a trade is CANCELLED with a side of BUY`() = runBlocking {
    val testCase = TestCase(
      numberOfBuys = 2L,
      numberOfSells = 0L,
      tradeQuantity = 5L,
      tradePrice = 100.00,
      tradeStatus = TradeStatus.ALLOCATED,
      instrumentId = "1",
      numberOfBuyCancels = 1
    )
    insertTrades(testCase)
    cancelTrades(testCase)
    delay(3000)

    val actualPosition = entityDb.getBulk<Position>().toList()[0]

    assertEquals("1", actualPosition.instrumentId)
    assertEquals(5, actualPosition.quantity)
    assertEquals(500.0, actualPosition.notional)
    assertEquals(500.0, actualPosition.value)
    assertEquals(5, actualPosition.buyQuantity)
    assertEquals(0, actualPosition.sellQuantity)
  }

  @Test
  fun `position is consolidated when a trade is CANCELLED with a side of SELL`() = runBlocking {
    val testCase = TestCase(
      numberOfBuys = 0L,
      numberOfSells = 2L,
      tradeQuantity = 5L,
      tradePrice = 100.00,
      tradeStatus = TradeStatus.ALLOCATED,
      instrumentId = "1",
      numberOfSellCancels = 1
    )
    insertTrades(testCase)
    cancelTrades(testCase)
    delay(3000)

    val actualPosition = entityDb.getBulk<Position>().toList()[0]

    assertEquals("1", actualPosition.instrumentId)
    assertEquals(-5, actualPosition.quantity)
    assertEquals(-500.0, actualPosition.notional)
    assertEquals(-500.0, actualPosition.value)
    assertEquals(0, actualPosition.buyQuantity)
    assertEquals(5, actualPosition.sellQuantity)
  }

  @Test
  fun `positions are grouped by instrumentId`() = runBlocking {
    insertTrades(TestCase(1L, 0L, 10L, 50.00, TradeStatus.NEW, "1"))
    insertTrades(TestCase(1L, 0L, 10L, 50.00, TradeStatus.NEW, "2"))
    delay(3000)
    val positions = entityDb.getBulk<Position>().toList()
    assertEquals("1", positions[0].instrumentId)
    assertEquals("2", positions[1].instrumentId)
  }

  @Test
  fun `chart data is consolidated when trades are inserted`() = runBlocking {
    val testCase = TestCase(
      numberOfBuys = 5L,
      numberOfSells = 2L,
      tradeQuantity = 5L,
      tradePrice = 100.00,
      tradeStatus = TradeStatus.NEW,
      instrumentId = "1",
      numberOfBuyCancels = 1
    )
    insertTrades(testCase)
    delay(3000)

    val chartsDataRecords = entityDb.getBulk<ChartsData>().toList()
    val actualBuy = chartsDataRecords.groupBy { it.side }[Side.BUY]?.get(0)
    val actualSell = chartsDataRecords.groupBy { it.side }[Side.SELL]?.get(0)

    assertEquals(25, actualBuy?.instrumentSideAllocation)
    assertEquals(Side.BUY, actualBuy?.side)
    assertEquals("1", actualBuy?.instrumentId)
    assertEquals(10, actualSell?.instrumentSideAllocation)
    assertEquals(Side.SELL, actualSell?.side)
    assertEquals("1", actualSell?.instrumentId)
  }

  @Test
  fun `chart data is grouped by instrumentId and side`() = runBlocking {
    insertTrades(TestCase(1L, 0L, 10L, 50.00, TradeStatus.NEW, "1"))
    insertTrades(TestCase(0L, 1L, 10L, 50.00, TradeStatus.NEW, "1"))
    delay(3000)
    val chartsDataRecords = entityDb.getBulk<ChartsData>().toList()
    assertEquals("1|BUY", chartsDataRecords[0].chartsDataId)
    assertEquals("1|SELL", chartsDataRecords[1].chartsDataId)
  }

  private fun insertTrades(testCase: TestCase) = runBlocking {
    val trades = arrayListOf<Trade>()
    val numberOfBuys = testCase.numberOfBuys
    val qtyOfBuyAndSellsCombined = testCase.numberOfBuys.plus(testCase.numberOfSells)
    for (i in 1..qtyOfBuyAndSellsCombined) {
        val tradeSide = if (i <= numberOfBuys) Side.BUY else Side.SELL
        trades.add(Trade {
        instrumentId = testCase.instrumentId
        counterpartyId = "1"
        price = testCase.tradePrice
        quantity = testCase.tradeQuantity
        side = tradeSide
        tradeStatus = testCase.tradeStatus
      })
    }
    entityDb.insertAll(trades)
  }

  private fun cancelTrades(testCase: TestCase) = runBlocking {
    val tradesMap = entityDb.getBulk<Trade>().toList().groupBy { it.side }
    for ((key, value) in tradesMap) {
      val numOfCurrentSideCancels = if (key == Side.BUY) testCase.numberOfBuyCancels else testCase.numberOfSellCancels
      for (i in 0..value.size) {
        if (i < numOfCurrentSideCancels)
          entityDb.updateBy(Trade.byId(value[i].tradeId)) { tradeStatus = TradeStatus.CANCELLED }
      }
    }
  }

  private data class TestCase(
    val numberOfBuys: Long,
    val numberOfSells: Long,
    val tradeQuantity: Long,
    val tradePrice: Double,
    val tradeStatus: TradeStatus,
    val instrumentId: String,
    val numberOfBuyCancels: Long = 0L,
    val numberOfSellCancels: Long = 0L
  )
}
