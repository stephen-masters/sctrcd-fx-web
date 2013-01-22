package uk.co.scattercode.iban;

import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.runtime.StatelessKnowledgeSession;
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

    private KnowledgeBase kbase;
    
    public final List<Country> countries = new ArrayList<Country>();
    
    public RuleBasedIbanValidator() {
        this.kbase = DroolsUtil.createKnowledgeBase(
                new DroolsResource[]{ 
                        new DroolsResource("rules/sctrcd/iban/IbanRules.drl", 
                                ResourcePathType.CLASSPATH, 
                                ResourceType.DRL)
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
	    
	    StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
	    ksession.setGlobal("countryList", countries);
	    
	    TrackingAgendaEventListener agendaEventListener = 
	            new TrackingAgendaEventListener();
	    TrackingWorkingMemoryEventListener workingMemoryEventListener = 
	            new TrackingWorkingMemoryEventListener();
	    ksession.addEventListener(agendaEventListener);
	    ksession.addEventListener(workingMemoryEventListener);
	    
	    IbanValidationRequest req = new IbanValidationRequest(iban);
	    
	    List<Object> facts = new ArrayList<Object>();
	    facts.add(req);
	    
		ksession.execute(facts);
		
		return result;
	}    

}
