package global.genesis.position.samples.events.rxjava;

import global.genesis.commons.annotation.Module;
import global.genesis.eventhandler.typed.rx3.Rx3ValidatingEventHandler;
import global.genesis.gen.dao.Company;
import global.genesis.message.core.event.Event;
import global.genesis.message.core.event.EventReply;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;

@Module
public class TestCompanyHandlerRx3ValidateJava implements Rx3ValidatingEventHandler<Company, EventReply> {

  @NotNull
  @Override
  public Single<EventReply> onValidate(@NotNull Event<Company> message) {
    Company company = message.getDetails();
    // custom code block..
    return Single.just(new EventReply.EventAck());
  }

  @NotNull
  @Override
  public Single<EventReply> onCommit(@NotNull Event<Company> message) {
    Company company = message.getDetails();
    // custom code block..
    return Single.just(new EventReply.EventAck());
  }
}
