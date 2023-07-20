package global.genesis.position.samples.events.rxjava

import global.genesis.commons.annotation.Module
import global.genesis.eventhandler.typed.rx3.Rx3ValidatingEventHandler
import global.genesis.gen.dao.Company
import global.genesis.message.core.event.Event
import global.genesis.message.core.event.EventReply
import io.reactivex.rxjava3.core.Single

@Module
class TestCompanyHandlerRx3Validate : Rx3ValidatingEventHandler<Company, EventReply> {
  override fun onValidate(message: Event<Company>): Single<EventReply> {
    val company = message.details
    // custom code block..
    return ack()
  }

  override fun onCommit(message: Event<Company>): Single<EventReply> {
    val company = message.details
    // custom code block..
    return ack()
  }
}
