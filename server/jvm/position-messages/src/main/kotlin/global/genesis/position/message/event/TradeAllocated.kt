package global.genesis.position.message.event

import global.genesis.message.core.annotation.Mandatory

data class TradeAllocated(
  @Mandatory
  val tradeId: String
)
