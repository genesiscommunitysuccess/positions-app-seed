package global.genesis.position.samples.events.rxjava;

import com.google.inject.Inject;
import global.genesis.commons.annotation.Module;
import global.genesis.commons.auth.Authority;
import global.genesis.db.rx.RxDb;
import global.genesis.db.rx.entity.multi.RxEntityDb;
import global.genesis.eventhandler.typed.rx3.Rx3EventHandler;
import global.genesis.gen.dao.Counterparty;
import global.genesis.gen.dao.Instrument;
import global.genesis.gen.dao.Trade;
import global.genesis.message.core.error.StandardError;
import global.genesis.message.core.event.Event;
import global.genesis.message.core.event.EventReply;
import global.genesis.session.AuthCache;
import global.genesis.session.RightSummaryCache;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Module
public class EventTrade implements Rx3EventHandler<Trade, EventReply> {

    private final RxEntityDb entityDb;
    private final RxDb rxDb;
    private final RightSummaryCache rightSummaryCache;

    private Authority authCache;

    @Inject
    public EventTrade(RxEntityDb entityDb, RxDb rxDb, RightSummaryCache rightSummaryCache) {
        this.entityDb = entityDb;
        this.rightSummaryCache = rightSummaryCache;
        this.rxDb = rxDb;
        this.authCache = AuthCache.newReader("ENTITY_VISIBILITY", rxDb.getUpdateQueue());
    }


    @Nullable
    @Override
    public String messageType() {
        return "TRADE_INSERT_JAVA";
    }

    @Override
    public Single<EventReply> process(Event<Trade> tradeEvent) {
        String userName = tradeEvent.getUserName();

        if(rightSummaryCache.userHasRight(userName, "TRADER")){
            Trade trade = tradeEvent.getDetails();
            if(authCache.isAuthorised(trade.getCounterpartyId(), userName)) {
                if (entityDb.get(Counterparty.byId(trade.getCounterpartyId())).blockingGet() == null) {
                    return Single.just(new StandardError("INTERNAL_ERROR",
                            "COUNTERPARTY ById(counterpartyId=" + trade.getCounterpartyId() + ") not found in database").toEventNackError());
                } else if (entityDb.get(Instrument.byId(trade.getInstrumentId())).blockingGet() == null) {
                    return Single.just(new StandardError("INTERNAL_ERROR",
                            "INSTRUMENT ById(instrumentId=" + trade.getInstrumentId() + ") not found in database").toEventNackError());
                } else {
                    return entityDb.writeTransaction(txn -> {
                        Trade result = txn.insert(trade).blockingGet().getRecord();
                        return ack(this, List.of(Map.of("TRADE_ID", result.getTradeId())));
                    }).map(result -> result.getFirst());
                }
            }
        }
        return Single.just(new StandardError("NOT_AUTHORISED", "User " + userName + " lacks sufficient permissions").toEventNackError());
    }

}
