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
import uk.co.scattercode.iban.facts.IbanValidationRequest;

@Service("ruleBasedIbanValidator")
public class RuleBasedIbanValidator implements IbanValidator {

    private DroolsResource[] resources = new DroolsResource[]{ 
            new DroolsResource("rules/sctrcd/iban/IbanRules.drl", 
                    ResourcePathType.CLASSPATH, 
                    ResourceType.DRL)
    };

    private KnowledgeBase kbase;
    
    public final List<String> isoCodes = new ArrayList<String>();
    
    public RuleBasedIbanValidator() {
        this.kbase = DroolsUtil.createKnowledgeBase(
                resources, 
                EventProcessingOption.CLOUD);
        for (CountryEnum c : CountryEnum.values()) {
            isoCodes.add(c.isoCode);
        }
    }
    
	@Override
	public IbanValidationResult validate(String iban) {
	    StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
	    ksession.setGlobal("countrylist", isoCodes);
	    
	    TrackingAgendaEventListener agendaEventListener = 
	            new TrackingAgendaEventListener();
	    TrackingWorkingMemoryEventListener workingMemoryEventListener = 
	            new TrackingWorkingMemoryEventListener();
	    ksession.addEventListener(agendaEventListener);
	    ksession.addEventListener(workingMemoryEventListener);
	    
	    List<Object> facts = new ArrayList<Object>();
	    facts.add(new IbanValidationRequest(iban));
	    
		ksession.execute(facts);
		
		
		return null;
	}    

}
