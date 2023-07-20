import global.genesis.data.ingest.external.PostgresConfiguration
import global.genesis.data.ingest.transform.OperationType

val tableMapper = buildOutboundMap("outbound", TRADE) {
  TRADE {
    REMOTE_TRADE_ID {
      property = "trade_id"
    }
    TRADE_ID {
      property = "genesis_trade_id"
    }
    INSTRUMENT_ID {
      property = "instrument_id"
    }
    COUNTERPARTY_ID {
      property = "counterparty_id"
    }
    QUANTITY {
      property = "quantity"
    }
    SIDE {
      property = "side"
    }
    PRICE {
      property = "price"
    }
  }
}

val databaseConfig = PostgresConfiguration(
  databaseName = "postgres",
  hostname = "localhost",
  port = 5432,
  username = "postgres",
  password = "mysecretpassword"
)

val inboundMap = buildInboundMap("", TRADE) {
  TRADE {
    REMOTE_TRADE_ID {
      property = "trade_id"
    }
    TRADE_ID {
      transform {
        val entity = entityDb.get(TradeLookup.byRemoteTradeId(input.get(stringValue("trade_id"))))
        entity?.tradeId
      }
    }
    INSTRUMENT_ID
    COUNTERPARTY_ID
    QUANTITY
    SIDE
    PRICE
  }
}

pipelines {
  // IN
  postgresSource("inbound") {
      hostname = "localhost"
      port = 5432
      username = "postgres"
      password = "mysecretpassword"
      databaseName = "postgres"

      table {
        "public.intable" to inboundMap.sink {
          when (operation) {
            OperationType.INSERT -> {
              val insertResult = entityDb.insert(mappedEntity)
              entityDb.insert(TradeLookup {
                tradeId = insertResult.record.tradeId
                remoteTradeId = insertResult.record.remoteTradeId!!
              })
            }
            OperationType.DELETE -> {
              entityDb.delete(mappedEntity)
              entityDb.delete(TradeLookup.byRemoteTradeId(mappedEntity.remoteTradeId!!))
            }
            OperationType.MODIFY -> {
              val modifyResult = entityDb.modify(mappedEntity)
              entityDb.modify(TradeLookup {
                tradeId = modifyResult.record.tradeId
                remoteTradeId = modifyResult.record.remoteTradeId!!
              })
            }
            OperationType.UNKNOWN -> {
              LOG.error("Received unknown operation type {}", operation)
            }
          }
        }
      }
  }

  // OUT
  genesisTableSource(TRADE) {
    key = TRADE.BY_ID

    map(tableMapper)
      .sink(databaseConfig) {
        onInsert = insertInto("outtable")
        onModify = updateTable ("outtable")
        onDelete = deleteFrom ("outtable")
      }
  }
}
