package global.genesis.position.samples.events.async

import com.google.inject.Inject
import global.genesis.commons.annotation.Module
import global.genesis.db.rx.entity.multi.AsyncEntityDb
import global.genesis.eventhandler.typed.async.AsyncEventHandler
import global.genesis.gen.dao.Company
import global.genesis.message.core.event.Event
import global.genesis.message.core.event.EventReply

@Module
class EventCompanyHandlerAsync @Inject constructor(
  private val entityDb: AsyncEntityDb,
) : AsyncEventHandler<Company, EventReply> {
  override suspend fun process(message: Event<Company>): EventReply {
    val company = message.details
    // custom code block..
    return EventReply.EventAck()
  }
}
