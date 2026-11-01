package uk.co.scattercode.iban;

import java.util.ArrayList;
import java.util.List;
import org.kie.api.KieBase;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.stereotype.Service;
import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.DroolsUtil;
import uk.co.scattercode.drools.util.ResourcePathType;
import uk.co.scattercode.drools.util.TrackingAgendaEventListener;
import uk.co.scattercode.drools.util.TrackingWorkingMemoryEventListener;
import uk.co.scattercode.iban.enums.CountryEnum;
import uk.co.scattercode.iban.facts.Country;
import uk.co.scattercode.iban.facts.IbanValidationRequest;

@Service("ruleBasedIbanValidator")
public class RuleBasedIbanValidator implements IbanValidator {

    private final KieBase kbase;

    public final List<Country> countries = new ArrayList<>();

    public RuleBasedIbanValidator() {
        this.kbase = DroolsUtil.createKnowledgeBase(
                new DroolsResource[] {
                    new DroolsResource("rules/sctrcd/iban/IbanRules.drl", ResourcePathType.CLASSPATH, ResourceType.DRL)
                },
                EventProcessingOption.CLOUD);
        for (CountryEnum c : CountryEnum.values()) {
            countries.add(new Country(c.isoCode, c.name));
        }
    }

    @Override
    public IbanValidationResult validate(String iban) {
        IbanValidationResult result = new IbanValidationResult(iban);

        // Perform an initial mod-97 check.
        if (!Mod97Check.isValid(IbanUtil.sanitize(iban))) {
            // The request is not valid at the most basic level, so there's no
            // point in proceeding with running it through the rules engine.
            result.setValid(false);
            return result;
        }

        StatelessKieSession ksession = kbase.newStatelessKieSession();
        ksession.setGlobal("countryList", countries);

        TrackingAgendaEventListener agendaEventListener = new TrackingAgendaEventListener();
        TrackingWorkingMemoryEventListener workingMemoryEventListener = new TrackingWorkingMemoryEventListener();
        ksession.addEventListener(agendaEventListener);
        ksession.addEventListener(workingMemoryEventListener);

        IbanValidationRequest req = new IbanValidationRequest(iban);

        List<Object> facts = new ArrayList<>();
        facts.add(req);

        ksession.execute(facts);

        // The rules annotate the request; any rejection makes the result invalid.
        result.setAnnotations(req.getAnnotations());

        return result;
    }
}
