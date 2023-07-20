package global.genesis.position.samples.events.rxjava;

import com.google.inject.Inject;
import global.genesis.commons.annotation.Module;
import global.genesis.db.rx.entity.multi.RxEntityDb;
import global.genesis.eventhandler.typed.rx3.Rx3EventHandler;
import global.genesis.gen.dao.Counterparty;
import global.genesis.message.core.event.Event;
import global.genesis.message.core.event.EventReply;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.Nullable;

@Module
public class EventCounterParty implements Rx3EventHandler<Counterparty, EventReply> {

    private final RxEntityDb entityDb;

    @Inject
    public EventCounterParty(RxEntityDb entityDb) {
        this.entityDb = entityDb;
    }

    @Nullable
    @Override
    public String messageType() {
        return "COUNTERPARTY_INSERT_JAVA";
    }

    @Override
    public Single<EventReply> process(Event<Counterparty> counterpartyEvent) {
        Counterparty counterparty = counterpartyEvent.getDetails();
        return entityDb.writeTransaction(txn -> {
           txn.insert(counterparty);
           return ack(this);
        }).map(result -> result.getFirst());
    }
}