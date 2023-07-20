package global.genesis.position.samples.events.rxjava;

import global.genesis.commons.annotation.Module;
import global.genesis.eventhandler.typed.rx3.Rx3EventHandler;
import global.genesis.gen.dao.Company;
import global.genesis.message.core.event.Event;
import global.genesis.message.core.event.EventReply;
import io.reactivex.rxjava3.core.Single;

@Module
public class TestCompanyHandlerRx3Java implements Rx3EventHandler<Company, EventReply> {

  @Override
  public Single<EventReply> process(Event<Company> companyEvent) {
    // custom block
    return Single.just(new EventReply.EventAck());
  }
}
