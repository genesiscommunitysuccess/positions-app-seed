import global.genesis.fix.messagespec.DateConvertersImpl.asLocalDateTime
import global.genesis.quickfix.fix44ref.field.Side

plugins {
  plugin(FixXlatorPlugin)
}

fixConfiguration {
  version = fix44ref
}

fun formatExecutionReport(input: GenesisSet): ExecutionReport {
  val executionReport = ExecutionReport()
  executionReport.set(OrderID("${input.getLong("TRADE_ID")}-SYNTH"))

  val noPartyIDsGroup = ExecutionReport.NoPartyIDs()
  noPartyIDsGroup.set(PartyID(input.getString("COUNTERPARTY_ID")))
  noPartyIDsGroup.set(PartyIDSource(PartyIDSource.PROPRIETARY_CUSTOM_CODE))
  noPartyIDsGroup.set(PartyRole(PartyRole.ENTERING_FIRM))
  executionReport.addGroup(noPartyIDsGroup)

  noPartyIDsGroup.set(PartyID(input.getString("ENTERED_BY")))
  noPartyIDsGroup.set(PartyIDSource(PartyIDSource.PROPRIETARY_CUSTOM_CODE))
  noPartyIDsGroup.set(PartyRole(PartyRole.ENTERING_TRADER))
  executionReport.addGroup(noPartyIDsGroup)

  executionReport.set(ExecID(input.getLong("TRADE_ID").toString()))
  executionReport.set(ExecType(ExecType.TRADE))
  executionReport.set(OrdStatus(OrdStatus.FILLED))

  val instrument = Instrument()
  instrument.set(Symbol(input.getString("INSTRUMENT_ID")))
  instrument.set(SecurityID(input.getString("INSTRUMENT_ID")))
  executionReport.set(instrument)

  executionReport.set(Side(deriveFIXSide(input.getString("SIDE"))))

  val quantity = input.getBigDecimal("QUANTITY")
  if (quantity != null) {
    executionReport.set(OrderQty(quantity))
    executionReport.set(LastQty(quantity))
    executionReport.set(CumQty(quantity))
  }
  executionReport.set(LeavesQty(0.0))

  val price = input.getDouble("PRICE")
  if (price != null) {
    executionReport.set(Price(price))
    executionReport.set(LastPx(price))
  }

  val dateTime = input.getDate("TRADE_DATETIME")
  if (dateTime != null) {
    executionReport.set(TransactTime(dateTime.asLocalDateTime()))
  }

  return executionReport
}

fun deriveFIXSide(side: String?): Char {
  return when (side) {
    "BUY" -> Side.BUY
    "B" -> Side.BUY
    "SELL" -> Side.SELL
    "S" -> Side.SELL
    else -> Side.UNDISCLOSED
  }
}

streamerClients {
  streamerClient("EXEC_REPORTS") {
    dataSource(processName = "POSITION_FGW_STREAMER", sourceName = "NEW_TRADES")
    isReplayable = true
    onMessage {
      send(targetProcess = "POSITION_FGW", messageType = "EVENT_SEND_RAW_FIX_MESSAGE") { input, output ->
        output["FIX_DATA"] = formatExecutionReport(input).toString()
      }
    }
  }
}
