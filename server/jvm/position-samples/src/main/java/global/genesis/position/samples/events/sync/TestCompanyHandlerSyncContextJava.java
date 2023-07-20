package global.genesis.position.samples.events.sync;

import global.genesis.eventhandler.typed.sync.SyncContextValidatingEventHandler;
import global.genesis.gen.dao.Company;
import global.genesis.message.core.event.Event;
import global.genesis.message.core.event.EventReply;
import global.genesis.message.core.event.ValidationResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestCompanyHandlerSyncContextJava implements SyncContextValidatingEventHandler<Company, EventReply, String> {

  @NotNull
  @Override
  public ValidationResult<EventReply, String> onValidate(@NotNull Event<Company> event) {
    Company company = event.getDetails();
    // custom code block..
    String companyName = company.getCompanyName();
    return validationResult(new EventReply.EventAck(), companyName);
  }

  @NotNull
  @Override
  public EventReply onCommit(@NotNull Event<Company> event, @Nullable String context) {
    if (!context.isEmpty()) {
      // Do something with the context
    }
    Company company = event.getDetails();
    // custom code block..
    return new EventReply.EventAck();
  }
}
