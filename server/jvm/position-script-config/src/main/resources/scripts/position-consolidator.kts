import global.genesis.gen.config.tables.CHARTS_DATA.INSTRUMENT_SIDE_ALLOCATION
import global.genesis.gen.config.tables.POSITION
import global.genesis.gen.config.tables.POSITION.BUY_QUANTITY
import global.genesis.gen.config.tables.POSITION.QUANTITY
import global.genesis.gen.config.tables.POSITION.SELL_QUANTITY
import global.genesis.gen.config.tables.POSITION.VALUE
import global.genesis.gen.config.view.TRADE_VIEW
import global.genesis.gen.dao.Position

consolidators {
  consolidator("CONSOLIDATE_POSITIONS", TRADE_VIEW, POSITION) {
    select {
      sum {
        when(side) {
          Side.BUY -> when(tradeStatus) {
            TradeStatus.NEW -> quantity
            TradeStatus.ALLOCATED -> quantity
            TradeStatus.CANCELLED -> 0
          }
          Side.SELL -> when(tradeStatus) {
            TradeStatus.NEW -> -quantity
            TradeStatus.ALLOCATED -> -quantity
            TradeStatus.CANCELLED -> 0
          }
        }
      } into QUANTITY
      sum {
        when(tradeStatus) {
          TradeStatus.NEW -> quantity
          TradeStatus.ALLOCATED -> quantity
          TradeStatus.CANCELLED -> 0
        }
      } onlyIf { side == Side.BUY } into BUY_QUANTITY
      sum {
        when(tradeStatus) {
          TradeStatus.NEW -> quantity
          TradeStatus.ALLOCATED -> quantity
          TradeStatus.CANCELLED -> 0
        }
      } onlyIf { side == Side.SELL }  into SELL_QUANTITY
      sum {
        when(tradeStatus) {
          TradeStatus.CANCELLED -> 0.0
          else -> {
            val quantity = when(side) {
              Side.BUY -> quantity
              Side.SELL -> -quantity
            }
            quantity * price
          }
        }
      } into VALUE
    }
    onCommit {
      val quantity = output.quantity ?: 0
      output.notional = input.price * quantity
      output.pnl = output.value - output.notional
    }
    groupBy {
      instrumentId
    } into {
      lookup {
        Position.ByInstrumentId(groupId)
      }
      build {
        Position {
          instrumentId = groupId
          quantity = 0
          value = 0.0
          pnl = 0.0
          notional = 0.0
          buyQuantity = 0
          sellQuantity = 0
        }
      }
    }
  }

  consolidator("CONSOLIDATE_CHART_DATA", TRADE, CHARTS_DATA) {
    select {
      sum {
        quantity
      } into INSTRUMENT_SIDE_ALLOCATION
    }
    onCommit {
      output.side = input.side
      output.instrumentId = input.instrumentId
    }
    groupBy {
      group(instrumentId, side)
    } into {
      lookup {
        ChartsData.byId(groupId)
      }
      build {
        ChartsData {
          chartsDataId = groupId
          side = side
          instrumentId = Trade::instrumentId.toString()
          instrumentSideAllocation = 0
        }
      }
    }
  }
}
