/**
 *
 *   System              : position
 *   Sub-System          : position Configuration
 *   Version             : 1.0
 *   Copyright           : (c) GENESIS
 *   Date                : 2021-09-07
 *
 *   Function : Provide Data Server Configuration for position.
 *
 *   Modification History
 *
 */

dataServer {

  query("ALL_TRADES", TRADE_VIEW) {
    permissioning {
      permissionCodes = listOf("TRADER", "SUPPORT")
      auth(mapName = "ENTITY_VISIBILITY") {
        TRADE_VIEW.COUNTERPARTY_ID
      }
    }
    indices {
      unique {
        QUANTITY
        TRADE_ID
      }
    }
  }
  query("ALL_POSITIONS", POSITION_VIEW) {
    config {
      backwardsJoins = true
    }
    permissioning {
      permissionCodes = listOf("TRADER", "SUPPORT")
    }
    fields {
      INSTRUMENT_ID
      INSTRUMENT_NAME
      POSITION_ID
      QUANTITY
      NOTIONAL
      PNL
      VALUE
      CURRENCY
    }
  }
  query("ALL_CHART_DATA", CHARTS_DATA) {
    config {
      batchingPeriod = 2000 // 2 seconds
    }
    permissioning {
      permissionCodes = listOf("TRADER", "SUPPORT")
    }
    fields {
      INSTRUMENT_SIDE_ALLOCATION
      SIDE
      INSTRUMENT_ID
    }
  }
}
