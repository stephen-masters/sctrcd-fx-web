package uk.co.scattercode.drools.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEvent;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When validation rules fire, they should insert a TradeValidationAnnotation
 * into the working memory. This class listens for these events, and adds them
 * to a list so that a client can query all the alerts raised for a request.
 * <p>
 * Drools 5 called this the working memory and a removal a retraction; the KIE
 * API calls them the rule runtime and a deletion. The class keeps its old name.
 * </p>
 * <p>
 * You should probably avoid using this outside testing, as the contained lists
 * of events will grow and grow.
 * </p>
 *
 * @author Stephen Masters
 */
public class TrackingWorkingMemoryEventListener extends DefaultRuleRuntimeEventListener {

    private static final Logger log = LoggerFactory.getLogger(TrackingWorkingMemoryEventListener.class);

    private final List<RuleRuntimeEvent> allEvents = new ArrayList<>();
    private final List<ObjectInsertedEvent> insertions = new ArrayList<>();
    private final List<ObjectDeletedEvent> retractions = new ArrayList<>();
    private final List<ObjectUpdatedEvent> updates = new ArrayList<>();

    private final List<Map<String, String>> factChanges = new ArrayList<>();

    private final FactHandle handleFilter;
    private final Class<?> classFilter;

    /**
     * Void constructor sets the listener to record all working memory events
     * with no filtering.
     */
    public TrackingWorkingMemoryEventListener() {
        this.handleFilter = null;
        this.classFilter = null;
    }

    /**
     * Constructor which sets up an event filter. The listener will only record
     * events when the event {@link FactHandle} matches the constructor argument.
     *
     * @param handle
     *            The {@link FactHandle} to filter on.
     */
    public TrackingWorkingMemoryEventListener(FactHandle handle) {
        this.handleFilter = handle;
        this.classFilter = null;
    }

    public TrackingWorkingMemoryEventListener(Class<?> classFilter) {
        this.handleFilter = null;
        this.classFilter = classFilter;
    }

    private boolean accepts(FactHandle handle, Object fact) {
        return (handleFilter == null && classFilter == null)
                || handle == handleFilter
                || fact.getClass().equals(classFilter);
    }

    @Override
    public void objectInserted(final ObjectInsertedEvent event) {
        if (accepts(event.getFactHandle(), event.getObject())) {
            insertions.add(event);
            allEvents.add(event);
            log.trace("Insertion: " + DroolsUtil.objectDetails(event.getObject()));
        }
    }

    @Override
    public void objectDeleted(final ObjectDeletedEvent event) {
        if (accepts(event.getFactHandle(), event.getOldObject())) {
            retractions.add(event);
            allEvents.add(event);
            log.trace("Retraction: " + DroolsUtil.objectDetails(event.getOldObject()));
        }
    }

    @Override
    public void objectUpdated(final ObjectUpdatedEvent event) {
        if (accepts(event.getFactHandle(), event.getObject())) {
            updates.add(event);
            allEvents.add(event);

            Object fact = event.getObject();
            try {
                factChanges.add(BeanUtils.describe(fact));
            } catch (Exception e) {
                log.error("Unable to get object details for tracking: " + DroolsUtil.objectDetails(fact), e);
            }
            log.trace("Update: " + DroolsUtil.objectDetails(event.getObject()));
        }
    }

    public List<RuleRuntimeEvent> getAllEvents() {
        return allEvents;
    }

    public List<ObjectInsertedEvent> getInsertions() {
        return insertions;
    }

    public List<ObjectDeletedEvent> getRetractions() {
        return retractions;
    }

    public List<ObjectUpdatedEvent> getUpdates() {
        return updates;
    }

    public List<Map<String, String>> getFactChanges() {
        return factChanges;
    }

    public String getPrintableSummary() {
        return "TrackingWorkingMemoryEventListener: " + "insertions=["
                + insertions.size() + "], " + "retractions=["
                + retractions.size() + "], " + "updates=["
                + updates.size() + "]";
    }

    public String getPrintableDetail() {
        StringBuilder report = new StringBuilder(getPrintableSummary());

        for (ObjectInsertedEvent event : insertions) {
            report.append("\n" + DroolsUtil.objectDetails(event.getObject()));
        }
        for (ObjectDeletedEvent event : retractions) {
            report.append("\n" + DroolsUtil.objectDetails(event.getOldObject()));
        }
        for (ObjectUpdatedEvent event : updates) {
            report.append("\n" + DroolsUtil.objectDetails(event.getObject()));
        }

        return report.toString();
    }
}
