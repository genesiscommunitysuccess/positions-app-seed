import global.genesis.gen.config.fields.Fields

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

views {

  view("TRADE_VIEW", TRADE) {

    joins {
      joining(COUNTERPARTY) {
        on(TRADE.COUNTERPARTY_ID to COUNTERPARTY { COUNTERPARTY_ID })
      }
      joining(INSTRUMENT) {
        on(TRADE.INSTRUMENT_ID to INSTRUMENT { INSTRUMENT_ID })
      }
    }

    fields {
      TRADE.allFields()

      COUNTERPARTY.NAME withPrefix COUNTERPARTY
      INSTRUMENT.NAME withPrefix INSTRUMENT
      INSTRUMENT.CURRENCY_ID withAlias "CURRENCY"

      derivedField("CONSIDERATION", DOUBLE) {
        // I: F2*H2
        withInput(TRADE.QUANTITY, TRADE.PRICE) { QUANTITY, PRICE ->
          QUANTITY * PRICE
        }
      }
    }
  }

  view("POSITION_VIEW", POSITION) {

    joins {
      joining(ALT_INSTRUMENT_ID, backwardsJoin = true) {
        on(POSITION.INSTRUMENT_ID to ALT_INSTRUMENT_ID { INSTRUMENT_ID })
          .and(ALT_INSTRUMENT_ID { ALTERNATE_TYPE } to "REFINITIV")

          .joining(INSTRUMENT_L1_PRICE, backwardsJoin = true) {
            on(ALT_INSTRUMENT_ID.INSTRUMENT_CODE to INSTRUMENT_L1_PRICE { INSTRUMENT_CODE })
          }
      }

      joining(INSTRUMENT) {
        on(POSITION.INSTRUMENT_ID to INSTRUMENT { INSTRUMENT_ID })
      }
    }

    fields {
      POSITION.POSITION_ID
      POSITION.INSTRUMENT_ID
      POSITION.QUANTITY
      POSITION.NOTIONAL
      POSITION.VALUE
      POSITION.PNL

      INSTRUMENT.NAME withPrefix INSTRUMENT
      INSTRUMENT.CURRENCY_ID withAlias "CURRENCY"
    }
  }
}
