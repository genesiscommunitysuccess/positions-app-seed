package global.genesis

import global.genesis.db.util.AbstractDatabaseTest
import global.genesis.db.util.TestUtil
import global.genesis.dictionary.GenesisDictionary
import global.genesis.gen.dao.Trade
import global.genesis.gen.dao.enums.Side
import global.genesis.gen.view.entity.TradeView
import global.genesis.gen.view.repository.TradeViewRx3Repository
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

class TradeViewTest : AbstractDatabaseTest() {

  @Inject
  lateinit var tradeViewRepository: TradeViewRx3Repository

  override fun createMockDictionary(): GenesisDictionary = prodDictionary()

  @Before
  fun setup() {
    TestUtil.loadData(resolvePath("data/TEST_DATA.csv"), rxDb)
  }

  @Test
  fun `test empty data`() {
    val count = tradeViewRepository.getBulk().count().blockingGet()
    assert(count == 0L) { count }
  }

  @Test
  fun `test with single trade - use getBulk`() {
    val now = DateTime.now()
    val trade = buildTrade("1", now)

    rxEntityDb.insert(trade).blockingGet()

    val tradeViewList = tradeViewRepository.getBulk()
      .toList()
      .blockingGet()

    assert(tradeViewList.size == 1) { tradeViewList }

    val tradeView = tradeViewList.first()
    assert(tradeView.counterpartyName == "Testing AG") { tradeView }
    assert(tradeView.instrumentName == "FOO.L") { tradeView }
    assert(tradeView.tradeDatetime == now) { tradeView }
    assert(tradeView.price == 12.0) { tradeView }
    assert(tradeView.quantity == 100L) { tradeView }
    assert(tradeView.side == Side.BUY) { tradeView }
  }

  @Test
  fun `test with single trade - use get`() {
    val now = DateTime.now()
    val trade = buildTrade("1", now)

    rxEntityDb.insert(trade).blockingGet()

    val tradeView = tradeViewRepository.get(TradeView.ById("1")).blockingGet()
    assert(tradeView.counterpartyName == "Testing AG") { tradeView }
    assert(tradeView.instrumentName == "FOO.L") { tradeView }
    assert(tradeView.tradeDatetime == now) { tradeView }
    assert(tradeView.price == 12.0) { tradeView }
    assert(tradeView.quantity == 100L) { tradeView }
    assert(tradeView.side == Side.BUY) { tradeView }
  }

  @Test
  fun `test with multiple trades`() {
    rxEntityDb.insertAll(
      buildTrade("1"),
      buildTrade("2"),
      buildTrade("3"),
      buildTrade("4"),
      buildTrade("5"),
    ).blockingGet()

    val count = tradeViewRepository.getBulk().count().blockingGet()

    assert(count == 5L) { count }
  }

  private fun buildTrade(tradeId: String, now: DateTime = DateTime.now()) =
    Trade.builder()
      .setTradeDatetime(now)
      .setCounterpartyId("2") // COUNTERPARTY_NAME = "Testing AG"
      .setInstrumentId("1")   // INSTRUMENT_NAME = "FOO.L"
      .setPrice(12.0)
      .setQuantity(100)
      .setSide(Side.BUY)
      .setTradeId(tradeId)
      .build()
}
