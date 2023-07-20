package global.genesis.position.message.event

import global.genesis.gen.dao.enums.Side
import global.genesis.gen.dao.enums.TradeStatus
import global.genesis.message.core.annotation.LongMax
import global.genesis.message.core.annotation.LongMin

data class TradeModify(
  val tradeId: String,
  val instrumentId: String,
  val counterpartyId: String,
  @LongMin(1)
  @LongMax(Long.MAX_VALUE)
  val quantity: Long,
  val side: Side,
  val price: Double,
  val tradeDatetime: String,
  val enteredBy: String,
  val tradeStatus: TradeStatus,
)
