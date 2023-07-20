package global.genesis.position.samples.events.rxjava

import global.genesis.commons.annotation.Module
import global.genesis.eventhandler.typed.rx3.Rx3ContextValidatingEventHandler
import global.genesis.gen.dao.Company
import global.genesis.message.core.event.Event
import global.genesis.message.core.event.EventReply
import global.genesis.message.core.event.ValidationResult
import io.reactivex.rxjava3.core.Single

@Module
class TestCompanyHandlerRx3Context : Rx3ContextValidatingEventHandler<Company, EventReply, String> {
  override fun onValidate(message: Event<Company>): Single<ValidationResult<EventReply, String>> {
    val company = message.details
    // custom code block..
    val companyName = company.companyName
    return Single.just(validationResult(EventReply.EventAck(), companyName))
  }

  override fun onCommit(message: Event<Company>, context: String?): Single<EventReply> {
    if (context != null) {
      // Do something with the context
    }
    val company = message.details
    // custom code block..
    return ack()
  }
}
