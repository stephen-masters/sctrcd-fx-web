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
import uk.co.scattercode.iban.facts.IbanValidationAnnotation;
import uk.co.scattercode.iban.facts.IbanValidationRequest;

@Service("ruleBasedIbanValidator")
public class RuleBasedIbanValidator implements IbanValidator {

    private KnowledgeBase kbase;
    
    public final List<String> isoCodes = new ArrayList<String>();
    
    private SimpleIbanValidator simpleValidator = new SimpleIbanValidator();
    
    public RuleBasedIbanValidator() {
        this.kbase = DroolsUtil.createKnowledgeBase(
                new DroolsResource[]{ 
                        new DroolsResource("rules/sctrcd/iban/IbanRules.drl", 
                                ResourcePathType.CLASSPATH, 
                                ResourceType.DRL)
                }, 
                EventProcessingOption.CLOUD);
        for (CountryEnum c : CountryEnum.values()) {
            isoCodes.add(c.isoCode);
        }
    }
    
	@Override
	public IbanValidationResult validate(String iban) {
	    IbanValidationResult ivResult = simpleValidator.validate(iban);
	    
	    if (!ivResult.isValid()) {
            // The request is not valid at the most basic level, so there's no
            // point in proceeding with running it through the rules engine.
	        return ivResult;
	    }
	    
	    StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
	    ksession.setGlobal("countrylist", isoCodes);
	    
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
		
		for (IbanValidationAnnotation annotation : req.getAnnotations()) {
		    ivResult.addAnnotation(annotation);
		}
		
		return ivResult;
	}    

}
