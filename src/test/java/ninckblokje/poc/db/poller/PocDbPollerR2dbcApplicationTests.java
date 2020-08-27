package ninckblokje.poc.db.poller;

import ninckblokje.poc.db.poller.entity.PollRecord;
import ninckblokje.poc.db.poller.poller.PollRecordPoller;
import ninckblokje.poc.db.poller.repository.PollRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.ReactiveTransactionManager;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PocDbPollerR2dbcApplicationTests {

    @Autowired
    private PollRecordPoller poller;
    @Autowired
    private PollRepository repository;
    @Autowired
    private ReactiveTransactionManager rtm;

    @BeforeEach
    @Rollback(false)
    public void beforeEach() {
        var flux = Flux.empty();

        for (int i = 0; i < 20; i++) {
            flux = flux.mergeWith(repository.save(PollRecord.builder().value(UUID.randomUUID().toString()).build()));
        }

        flux.blockLast();
    }

    @Test
    void testPollLocking() throws InterruptedException, ExecutionException {
        assertEquals(20L, repository.count().block());

        var f1 = doPoll();
        var f2 = doPoll();

        var p1 = f1.get();
        var p2 = f2.get();

        var foundIds = new ArrayList<>();

        foundIds.addAll(p1.stream()
                .map(PollRecord::getId)
                .collect(Collectors.toList()));
        foundIds.addAll(p2.stream()
                .map(PollRecord::getId)
                .collect(Collectors.toList()));

        assertEquals(20, foundIds.size());

        var duplicateIds = foundIds.stream()
                .filter(foundId -> Collections.frequency(foundIds, foundId) > 1)
                .collect(Collectors.toList());
        assertTrue(duplicateIds.isEmpty());
    }

    Future<List<PollRecord>> doPoll() {
        return CompletableFuture.supplyAsync(() -> {
            var records = new ArrayList<PollRecord>();

            var disposable = poller.getNextPollCycle()
                    .subscribe(records::add);

            while (!disposable.isDisposed()) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return records;
        });
    }
}
