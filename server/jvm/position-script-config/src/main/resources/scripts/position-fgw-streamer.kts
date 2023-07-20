streams {
    stream("NEW_TRADES", TRADE_AUDIT.BY_TIMESTAMP) {
        maxLogons = 1
        logoffTimeout = 5000
        batchSize = 100
        where { record ->
            record.auditEventType == "EVENT_TRADE_INSERT" && record.tradeStatus == TradeStatus.NEW
        }
    }

    stream("BOOKED_TRADES", TRADE_AUDIT.BY_TIMESTAMP) {
        maxLogons = 1
        logoffTimeout = 5000
        batchSize = 100
        where { record ->
          record.auditEventType == "EVENT_TRADE_ALLOCATED" && record.tradeStatus == TradeStatus.ALLOCATED
        }
    }
}
