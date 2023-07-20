/**
 *
 *   System              : position
 *   Sub-System          : position Configuration
 *   Version             : 1.0
 *   Copyright           : (c) GENESIS
 *   Date                : 2021-09-07
 *
 *   Function : Provide Request Server configuration for position.
 *
 *   Modification History
 *
 */

requestReplies {

  requestReply("TRADE", TRADE_VIEW) {
    permissioning {
      permissionCodes = listOf("TRADER", "SUPPORT")
      auth(mapName = "ENTITY_VISIBILITY") {
        TRADE_VIEW.COUNTERPARTY_ID
      }
    }
  }
}
