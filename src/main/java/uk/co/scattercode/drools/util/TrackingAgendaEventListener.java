package uk.co.scattercode.drools.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A listener that will track all rule firings in a session.
 *
 * @author Stephen Masters
 */
public class TrackingAgendaEventListener extends DefaultAgendaEventListener {

    private static final Logger log = LoggerFactory.getLogger(TrackingAgendaEventListener.class);

    private final List<Activation> activationList = new ArrayList<>();

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        Rule rule = event.getMatch().getRule();

        String ruleName = rule.getName();
        Map<String, Object> ruleMetaDataMap = rule.getMetaData();

        activationList.add(new Activation(ruleName));
        StringBuilder sb = new StringBuilder("Rule fired: " + ruleName);

        if (!ruleMetaDataMap.isEmpty()) {
            sb.append("\n  With [" + ruleMetaDataMap.size() + "] meta-data:");
            for (Map.Entry<String, Object> metaData : ruleMetaDataMap.entrySet()) {
                sb.append("\n    key=" + metaData.getKey() + ", value=" + metaData.getValue());
            }
        }

        log.debug(sb.toString());
    }

    /**
     * Did a rule with exactly this name fire?
     */
    public boolean isRuleFired(String ruleName) {
        return activationList.stream().anyMatch(a -> a.getRuleName().equals(ruleName));
    }

    public void reset() {
        activationList.clear();
    }

    public final List<Activation> getActivationList() {
        return activationList;
    }

    public String activationsToString() {
        if (activationList.isEmpty()) {
            return "No activations occurred.";
        } else {
            StringBuilder sb = new StringBuilder("Activations: ");
            for (Activation activation : activationList) {
                sb.append("\n  rule: ").append(activation.getRuleName());
            }
            return sb.toString();
        }
    }
}
