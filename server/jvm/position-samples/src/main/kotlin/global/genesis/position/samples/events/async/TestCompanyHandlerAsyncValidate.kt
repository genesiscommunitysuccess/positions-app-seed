package global.genesis.position.samples.events.async

import global.genesis.commons.annotation.Module
import global.genesis.eventhandler.typed.async.AsyncValidatingEventHandler
import global.genesis.gen.dao.Company
import global.genesis.message.core.event.Event
import global.genesis.message.core.event.EventReply

@Module
class TestCompanyHandlerAsyncValidate : AsyncValidatingEventHandler<Company, EventReply> {
  override suspend fun onValidate(message: Event<Company>): EventReply {
    val company = message.details
    // custom code block..
    return ack()
  }

  override suspend fun onCommit(message: Event<Company>): EventReply {
    val company = message.details
    // custom code block..
    return ack()
  }
}
