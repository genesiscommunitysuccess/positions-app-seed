package global.genesis.position.message.event

import global.genesis.message.core.annotation.Mandatory

data class TradeCancel(
  @Mandatory
  val tradeId: String
)
