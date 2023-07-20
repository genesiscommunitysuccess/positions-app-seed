package global.genesis.position.samples.events.sync

import global.genesis.commons.annotation.Module
import global.genesis.eventhandler.typed.sync.SyncValidatingEventHandler
import global.genesis.gen.dao.Company
import global.genesis.message.core.event.Event
import global.genesis.message.core.event.EventReply

@Module
class TestCompanyHandlerSyncValidate : SyncValidatingEventHandler<Company, EventReply> {
  override fun onValidate(message: Event<Company>): EventReply {
    val company = message.details
    // custom code block
    return ack()
  }

  override fun onCommit(message: Event<Company>): EventReply {
    val company = message.details
    // custom code block
    return ack()
  }
}
