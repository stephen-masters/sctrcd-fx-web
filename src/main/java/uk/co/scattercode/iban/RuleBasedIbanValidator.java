package uk.co.scattercode.iban;

import org.drools.builder.ResourceType;
import org.drools.runtime.ObjectFilter;
import org.springframework.stereotype.Service;

import uk.co.scattercode.drools.util.AbstractStatefulRulesService;
import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.ResourcePathType;
import uk.co.scattercode.iban.facts.IbanValidationAnnotation;
import uk.co.scattercode.iban.facts.IbanValidationRequest;

@Service("ruleBasedIbanValidator")
public class RuleBasedIbanValidator 
        extends AbstractStatefulRulesService
		implements IbanValidator {

    @Override
    protected DroolsResource[] getResources() {
        return new DroolsResource[] { 
                new DroolsResource("rules/sctrcd/iban/IbanRules.drl", 
                        ResourcePathType.CLASSPATH, 
                        ResourceType.DRL)
        };
    }
    
	@Override
	public IbanValidationResult validate(String iban) {
		insert(new IbanValidationRequest(iban));
		fireAllRules();
		this.kenv.knowledgeSession.getObjects(new ObjectFilter() {
            @Override
            public boolean accept(Object fact) {
                return fact.getClass().getSimpleName()
                        .equals(IbanValidationAnnotation.class.getSimpleName());
            }
        });
		return null;
	}

}
