import global.genesis.TradeStateMachine
import global.genesis.commons.standards.GenesisPaths
import global.genesis.gen.view.repository.TradeViewAsyncRepository
import global.genesis.jackson.core.GenesisJacksonMapper
import global.genesis.message.core.event.Event
import global.genesis.position.message.event.TradeInsert
import java.io.File
import java.time.LocalDate

/**
 *
 *   System              : position
 *   Sub-System          : position Configuration
 *   Version             : 1.0
 *   Copyright           : (c) GENESIS
 *   Date                : 2021-09-07
 *
 *   Function : Provide Event Handler configuration for position.
 *
 *   Modification History
 *
 */
val tradeViewRepo = inject<TradeViewAsyncRepository>()

eventHandler {
  val stateMachine = inject<TradeStateMachine>()

  eventHandler<TradeInsert>(name = "TRADE_INSERT", transactional = true) {
    permissioning {
      permissionCodes = listOf("TRADER")
      auth(mapName = "ENTITY_VISIBILITY") {
        field { counterpartyId }
      }
    }
    onValidate { event ->
      val message = event.details
      verify {
        entityDb hasEntry Counterparty.ById(message.counterpartyId)
        entityDb hasEntry Instrument.ById(message.instrumentId)
      }
      ack()
    }
    onCommit { event ->
      val trade = event.details
      stateMachine.insert(entityDb, Trade {
        instrumentId = trade.instrumentId
        counterpartyId = trade.counterpartyId
        quantity = trade.quantity
        side = Side.valueOf(trade.side.toString())
        price = trade.price
        tradeDatetime = parseDateTime(trade.tradeDatetime)
        enteredBy = trade.enteredBy.ifBlank { event.userName }
        tradeStatus = trade.tradeStatus
      })
      entityDb.upsert(InstrumentPriceSubscription {
        instrumentCode = trade.instrumentId
        primaryExchangeId = "LSE"
      })
      ack()
    }
  }

  //When name is not specified, the name of the event will be derived from the meta class name -  EVENT_TRADE_CANCEL
  eventHandler<TradeCancel>(transactional = true) {
    onCommit { event ->
      val message = event.details
      stateMachine.modify(entityDb, message.tradeId) { trade ->
        trade.tradeStatus = TradeStatus.CANCELLED
      }
      ack()
    }
  }
  eventHandler<TradeAllocated>(transactional = true) {
    onCommit { event ->
      val message = event.details
      stateMachine.modify(entityDb, message.tradeId) { trade ->
        trade.tradeStatus = TradeStatus.ALLOCATED
      }
      ack()
    }
  }

  eventHandler<TradeModify>(name = "TRADE_MODIFY", transactional = true) {
    permissioning {
      permissionCodes = listOf("TRADER")
      auth(mapName = "ENTITY_VISIBILITY") {
        field { counterpartyId }
      }
    }
    onCommit { event ->
      val trade = event.details
      stateMachine.modify(entityDb, trade.tradeId) {
        it.tradeId = trade.tradeId
        it.instrumentId = trade.instrumentId
        it.counterpartyId = trade.counterpartyId
        it.quantity = trade.quantity
        it.side = Side.valueOf(trade.side.toString())
        it.price = trade.price
        it.tradeDatetime = parseDateTime(trade.tradeDatetime)
        it.enteredBy = trade.enteredBy
        it.tradeStatus = trade.tradeStatus
      }
      ack()
    }
  }

  eventHandler<PositionReport> {
    onCommit {
      val mapper = GenesisJacksonMapper.csvWriter<TradeView>()
      val today = LocalDate.now().toString()
      val positionReportFolder = File(GenesisPaths.runtime()).resolve("position-daily-report")
      if (!positionReportFolder.exists()) positionReportFolder.mkdirs()

      tradeViewRepo.getBulk()
        .toList()
        .groupBy { it.counterpartyName }
        .forEach { (counterParty, trades) ->
          val file = positionReportFolder.resolve("${counterParty}_$today.csv")
          if (file.exists()) file.delete()
          mapper.writeValues(file).use { it.writeAll(trades) }
        }

      ack()
    }
  }
}

fun parseDateTime(dateTimeString: String): DateTime {
  return try {
    DateTime.parse(dateTimeString)
  } catch (e: Exception) {
    now()
  }
}
