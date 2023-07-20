package global.genesis.position.samples.events.rxjava;

import global.genesis.commons.annotation.Module;
import global.genesis.eventhandler.typed.rx3.Rx3ContextValidatingEventHandler;
import global.genesis.gen.dao.Company;
import global.genesis.message.core.event.Event;
import global.genesis.message.core.event.EventReply;
import global.genesis.message.core.event.ValidationResult;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Module
public class TestCompanyHandlerRx3ContextJava implements Rx3ContextValidatingEventHandler<Company, EventReply, String> {

  @NotNull
  @Override
  public Single<EventReply> onCommit(@NotNull Event<Company> event, @Nullable String context) {
    if (!context.isEmpty()) {
      // Do something with the context
    }
    Company company = event.getDetails();
    // custom code block..
    return Single.just(new EventReply.EventAck());
  }

  @NotNull
  @Override
  public Single<ValidationResult<EventReply, String>> onValidate(@NotNull Event<Company> event) {
    Company company = event.getDetails();
    // custom code block..
    String companyName = company.getCompanyName();
    return Single.just(validationResult(new EventReply.EventAck(), companyName));
  }
}
