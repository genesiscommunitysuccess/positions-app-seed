package global.genesis.position.samples.events.sync;

import global.genesis.eventhandler.typed.sync.SyncValidatingEventHandler;
import global.genesis.gen.dao.Company;
import global.genesis.message.core.event.Event;
import global.genesis.message.core.event.EventReply;
import org.jetbrains.annotations.NotNull;

public class TestCompanyHandlerSyncValidateJava implements SyncValidatingEventHandler<Company, EventReply> {
  @NotNull
  @Override
  public EventReply onCommit(@NotNull Event<Company> event) {
    Company company = event.getDetails();
    // custom code block
    return new EventReply.EventAck();
  }

  @NotNull
  @Override
  public EventReply onValidate(@NotNull Event<Company> event) {
    Company company = event.getDetails();
    // custom code block
    return new EventReply.EventAck();
  }
}
