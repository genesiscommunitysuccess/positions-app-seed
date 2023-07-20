package global.genesis.position.samples.events.sync

import global.genesis.commons.annotation.Module
import global.genesis.eventhandler.typed.sync.SyncContextValidatingEventHandler
import global.genesis.gen.dao.Company
import global.genesis.message.core.event.Event
import global.genesis.message.core.event.EventReply
import global.genesis.message.core.event.ValidationResult

@Module
class TestCompanyHandlerSyncContext : SyncContextValidatingEventHandler<Company, EventReply, String> {
  override fun onValidate(message: Event<Company>): ValidationResult<EventReply, String> {
    val company = message.details
    // custom code block..
    val companyName = company.companyName
    return validationResult(ack(), companyName)
  }

  override fun onCommit(message: Event<Company>, context: String?): EventReply {
    if (context != null) {
      // Do something with the context
    }
    val company = message.details
    // custom code block..
    return ack()
  }
}
