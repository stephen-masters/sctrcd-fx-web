package uk.co.scattercode.iban;

import org.drools.builder.ResourceType;
import org.springframework.stereotype.Service;

import uk.co.scattercode.drools.util.AbstractStatefulRulesService;
import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.ResourcePathType;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValid(String iban) {
		// TODO Auto-generated method stub
		return false;
	}

}
