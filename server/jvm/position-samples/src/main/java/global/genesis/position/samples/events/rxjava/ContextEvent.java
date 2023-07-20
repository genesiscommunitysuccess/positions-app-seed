package global.genesis.position.samples.events.rxjava;

import com.google.inject.Inject;
import global.genesis.commons.annotation.Module;
import global.genesis.db.rx.entity.multi.RxEntityDb;
import global.genesis.eventhandler.typed.rx3.Rx3ContextValidatingEventHandler;
import global.genesis.gen.dao.Company;
import global.genesis.message.core.event.Event;
import global.genesis.message.core.event.EventReply;
import global.genesis.message.core.event.ValidationResult;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Module
public class ContextEvent implements Rx3ContextValidatingEventHandler<Company, EventReply, String> {
    private final RxEntityDb entityDb;

    @Inject
    public ContextEvent(RxEntityDb entityDb) {
        this.entityDb = entityDb;
    }

    @Nullable
    @Override
    public String messageType() {
        return "CONTEXT_COMPANY_INSERT";
    }

    @NotNull
    @Override
    public Single<EventReply> onCommit(@NotNull Event<Company> event, @Nullable String context) {
        String parsedContext;
        parsedContext = Objects.requireNonNullElse(context, "Missing context");
        Company company = event.getDetails();
        return entityDb.insert(company).flatMap(result -> ack(this, List.of(Map.of("VALUE",parsedContext))));
    }

    @NotNull
    @Override
    public Single<ValidationResult<EventReply, String>> onValidate(@NotNull Event<Company> event) {
        Company company = event.getDetails();
        if(company.getCompanyName().equals("MY_COMPANY")) {
            return ack(this).map(result -> validationResult(result, "Best company in the world"));
        } else {
            return ack(this).map(this::validationResult);
        }
    }
}
