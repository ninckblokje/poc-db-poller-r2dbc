package ninckblokje.poc.db.poller.repository;

import ninckblokje.poc.db.poller.entity.PollRecord;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PollRepository extends ReactiveCrudRepository<PollRecord, Long> {

    @Query("select TOP(10) pollrecord0_.id as id, pollrecord0_.value as value from poll_record pollrecord0_ with (updlock, rowlock, readpast) where pollrecord0_.value is not null")
    Flux<PollRecord> getFirst10ByValueIsNotNull();
}
