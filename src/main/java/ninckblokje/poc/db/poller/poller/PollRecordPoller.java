package ninckblokje.poc.db.poller.poller;

import lombok.extern.slf4j.Slf4j;
import ninckblokje.poc.db.poller.entity.PollRecord;
import ninckblokje.poc.db.poller.repository.PollRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

@Service
@Transactional
@Slf4j
public class PollRecordPoller {

    private final PollRepository repository;

    public PollRecordPoller(PollRepository repository) {
        this.repository = repository;
    }

    public Flux<PollRecord> getNextPollCycle() {
        log.info("Polling next 10 records");
        return repository.getFirst10ByValueIsNotNull();
    }
}
