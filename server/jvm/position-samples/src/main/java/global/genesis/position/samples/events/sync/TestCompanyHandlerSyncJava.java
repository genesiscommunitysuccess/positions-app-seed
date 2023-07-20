package global.genesis.position.samples.events.sync;

import global.genesis.eventhandler.typed.sync.SyncEventHandler;
import global.genesis.gen.dao.Company;
import global.genesis.message.core.event.Event;
import global.genesis.message.core.event.EventReply;

public class TestCompanyHandlerSyncJava implements SyncEventHandler<Company, EventReply> {
  @Override
  public EventReply process(Event<Company> companyEvent) {
    // custom code block
    return new EventReply.EventAck();
  }
}
