package global.genesis.position.samples.events.rxjava;

import global.genesis.gen.dao.RightSummary;
import global.genesis.gen.dao.Trade;
import global.genesis.gen.dao.enums.Side;
import global.genesis.message.core.error.GenesisError;
import global.genesis.message.core.error.StandardError;
import global.genesis.message.core.event.Event;
import global.genesis.message.core.event.EventReply;
import global.genesis.testsupport.AbstractGenesisTestSupport;
import global.genesis.testsupport.EventResponse;
import global.genesis.testsupport.GenesisTestConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TradingEventHandlerTestJava extends AbstractGenesisTestSupport<EventResponse> {

    public TradingEventHandlerTestJava() {
        super(GenesisTestConfig.builder()
                        .setPackageNames(List.of("global.genesis.eventhandler","global.genesis.position.samples.events.rxjava"))
                        .setGenesisHome("/GenesisHome/")
                        .setInitialDataFiles("TEST_DATA.csv")
                        .addAuthCacheOverride("ENTITY_VISIBILITY")
                        .setParser(EventResponse.Companion)
                        .build()
        );
    }

    @Before
    public void setUp() {
        authorise("ENTITY_VISIBILITY", "1", "TraderUser");
        getRxDb().insert(RightSummary.builder().setRightCode("TRADER").setUserName("TraderUser").build().toDbRecord()).blockingGet();
        getRxDb().insert(RightSummary.builder().setRightCode("SUPPORT").setUserName("SupportUser").build().toDbRecord()).blockingGet();
    }

    @Test
    public void testTradeInsertedByTrader() {
        Trade trade = Trade.builder()
                .setTradeId("1")
                .setCounterpartyId("1")
                .setInstrumentId("2")
                .setSide(Side.BUY)
                .setPrice(5.0)
                .setQuantity(1)
                .build();
        Event event = new Event(trade, "EVENT_TRADE_INSERT_JAVA", "TraderUser");
        EventReply reply = getMessageClient().request(event, EventReply.class).blockingGet();
        assertEquals(reply, new EventReply.EventAck(List.of(Map.of("TRADE_ID", trade.getTradeId()))));

        Trade insertedUser = getRxDb().entityDb().get(Trade.byId("1")).blockingGet();
        assertNotNull(insertedUser);
    }

    @Test
    public void testTradeCannotBeInsertedIfNotTrader() {
        Trade trade = Trade.builder()
                .setTradeId("1")
                .setCounterpartyId("1")
                .setInstrumentId("2")
                .setSide(Side.BUY)
                .setPrice(5.0)
                .setQuantity(1)
                .build();
        Event event = new Event(trade, "EVENT_TRADE_INSERT_JAVA", "SupportUser");
        EventReply reply = getMessageClient().request(event, EventReply.class).blockingGet();

        GenesisError genesisError = new StandardError("NOT_AUTHORISED",
                "User SupportUser lacks sufficient permissions");
        assertEquals(reply, new EventReply.EventNack(List.of(), List.of(genesisError)));
    }

    @Test
    public void testTradeInsertWrongInstrumentId() {
        Trade trade = Trade.builder()
                .setTradeId("1")
                .setCounterpartyId("1")
                .setInstrumentId("DOESNOTEXIST")
                .setSide(Side.BUY)
                .setPrice(1.0)
                .setQuantity(1)
                .build();
        Event event = new Event(trade, "EVENT_TRADE_INSERT_JAVA", "TraderUser");
        EventReply reply = getMessageClient().request(event, EventReply.class).blockingGet();

        GenesisError genesisError = new StandardError("INTERNAL_ERROR",
                "INSTRUMENT ById(instrumentId=DOESNOTEXIST) not found in database");
        assertEquals(reply, new EventReply.EventNack(List.of(), List.of(genesisError)));
    }
}
