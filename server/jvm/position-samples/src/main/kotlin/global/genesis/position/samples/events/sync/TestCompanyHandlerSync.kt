package global.genesis.position.samples.events.sync

import global.genesis.commons.annotation.Module
import global.genesis.eventhandler.typed.sync.SyncEventHandler
import global.genesis.gen.dao.Company
import global.genesis.message.core.event.Event
import global.genesis.message.core.event.EventReply

@Module
class TestCompanyHandlerSync : SyncEventHandler<Company, EventReply> {
  override fun process(message: Event<Company>): EventReply {
    // custom code block
    return ack()
  }
}
