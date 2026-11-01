package uk.co.scattercode.drools.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.scattercode.drools.util.testfacts.Customer;
import uk.co.scattercode.drools.util.testfacts.Product;

/**
 * A very simple test to confirm that the
 * {@link TrackingWorkingMemoryEventListener} is responding to insertions and
 * retractions.
 *
 * @author Stephen Masters
 */
class TrackingWorkingMemoryEventListenerTest {

    private static final Logger log = LoggerFactory.getLogger(TrackingWorkingMemoryEventListenerTest.class);

    private final KnowledgeEnvironment kenv = new KnowledgeEnvironment(new DroolsResource[] {
        new DroolsResource(
                "sctrcd/drools/util/TrackingWorkingMemoryEventListenerTest.drl",
                ResourcePathType.CLASSPATH,
                ResourceType.DRL)
    });

    private final ObjectInsertedEvent objectInsertedEvent = new StubObjectInsertedEvent("Mock object.");

    @Test
    void shouldTrackEvents() {
        TrackingWorkingMemoryEventListener listener = new TrackingWorkingMemoryEventListener();

        int insertionCountBeforeInsertion = listener.getInsertions().size();
        int retractionCountBeforeInsertion = listener.getRetractions().size();
        int updateCountBeforeInsertion = listener.getUpdates().size();

        listener.objectInserted(objectInsertedEvent);

        int insertionCountAfterInsertion = listener.getInsertions().size();
        int retractionCountAfterInsertion = listener.getRetractions().size();
        int updateCountAfterInsertion = listener.getUpdates().size();

        assertEquals(insertionCountBeforeInsertion + 1, insertionCountAfterInsertion);
        assertEquals(retractionCountBeforeInsertion, retractionCountAfterInsertion);
        assertEquals(updateCountBeforeInsertion, updateCountAfterInsertion);
    }

    @Test
    void shouldTrackFilteredUpdates() {

        TrackingAgendaEventListener agendaListener = new TrackingAgendaEventListener();
        TrackingWorkingMemoryEventListener workingMemoryListener = new TrackingWorkingMemoryEventListener();

        kenv.addEventListener(agendaListener);
        kenv.addEventListener(workingMemoryListener);

        FactHandle productHandle = kenv.insert(new Product("Book", 20));
        FactHandle customerHandle = kenv.insert(new Customer("Jimbo"));

        TrackingWorkingMemoryEventListener productListener = new TrackingWorkingMemoryEventListener(productHandle);
        kenv.addEventListener(productListener);
        TrackingWorkingMemoryEventListener customerListener = new TrackingWorkingMemoryEventListener(customerHandle);
        kenv.addEventListener(customerListener);

        kenv.fireAllRules();

        assertEquals(
                10,
                productListener.getUpdates().size(),
                "There should have been 10 updates, as the count was decremented from 20 to 10.");
        assertEquals(1, customerListener.getUpdates().size(), "There should only be one customer update.");
        assertEquals(11, agendaListener.getActivationList().size(), "Each update is one rule firing.");

        logChanges(productListener);
    }

    @Test
    void shouldTrackClassFilteredUpdates() {
        TrackingWorkingMemoryEventListener everythingListener = new TrackingWorkingMemoryEventListener();
        TrackingWorkingMemoryEventListener productListener = new TrackingWorkingMemoryEventListener(Product.class);
        kenv.addEventListener(everythingListener);
        kenv.addEventListener(productListener);

        kenv.insert(new Product("Book", 20));
        kenv.insert(new Customer("Jimbo"));

        kenv.fireAllRules();

        assertEquals(11, everythingListener.getUpdates().size(), "There should be 11 updates in total.");
        assertEquals(
                10,
                productListener.getUpdates().size(),
                "There should have been 10 updates, as the count was decremented from 20 to 10.");

        logChanges(productListener);
    }

    private static void logChanges(TrackingWorkingMemoryEventListener listener) {
        StringBuilder sb = new StringBuilder("The following changes to product were tracked:\n");
        for (Map<String, String> objectProperties : listener.getFactChanges()) {
            for (Map.Entry<String, String> property : objectProperties.entrySet()) {
                sb.append(property.getKey() + "=\"" + property.getValue() + "\" ");
            }
            sb.append("\n");
        }
        log.info(sb.toString());
    }
}
