/**
 *
 *   System              : position
 *   Sub-System          : position Configuration
 *   Version             : 1.0
 *   Copyright           : (c) GENESIS
 *   Date                : 2021-09-07
 *
 *   Function : Provide Dictionary configuration for position.
 *
 *   Modification History
 *
 */

tables {
  table (name = "TRADE", id = 11000, audit = details(id = 11003, sequence = "TR", tsKey = true)) {
    sequence(TRADE_ID, "TR")
    REMOTE_TRADE_ID
    INSTRUMENT_ID not null
    COUNTERPARTY_ID not null
    QUANTITY not null
    SIDE not null
    PRICE not null
    TRADE_DATETIME
    ENTERED_BY
    TRADE_STATUS

    primaryKey {
      TRADE_ID
    }
  }

  table(name= "POSITION", id = 11001) {
    sequence(POSITION_ID, "PS")
    INSTRUMENT_ID
    QUANTITY
    NOTIONAL
    VALUE
    PNL
    BUY_QUANTITY not null
    SELL_QUANTITY not null

    primaryKey {
     POSITION_ID
    }

    indices {
      unique {
        INSTRUMENT_ID
      }
    }
  }

  table(name = "COMPANY", id = 11002) {
    COMPANY_NAME
    COMPANY_LOCATION

    primaryKey {
      COMPANY_NAME
    }
  }

  table(name = "CHARTS_DATA", id = 11005) {
    sequence(CHARTS_DATA_ID, "CD")
    INSTRUMENT_SIDE_ALLOCATION not null
    SIDE not null
    INSTRUMENT_ID not null

    primaryKey {
      CHARTS_DATA_ID
    }
  }

  table(name = "TRADE_LOOKUP", id = 11006) {
    REMOTE_TRADE_ID not null
    TRADE_ID not null

    primaryKey {
      REMOTE_TRADE_ID
    }
    indices {
      unique {
        TRADE_ID
      }
    }
  }
}
