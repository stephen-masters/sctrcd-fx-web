package uk.co.scattercode.iban;

import org.drools.builder.ResourceType;
import org.springframework.stereotype.Service;

import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.KnowledgeEnvironment;
import uk.co.scattercode.drools.util.ResourcePathType;
import uk.co.scattercode.iban.facts.IbanValidationRequest;

@Service("ruleBasedIbanValidator")
public class RuleBasedIbanValidator implements IbanValidator {

    KnowledgeEnvironment kenv = new KnowledgeEnvironment(
            new DroolsResource[] { 
                    new DroolsResource("rules/sctrcd/iban/IbanRules.drl", 
                            ResourcePathType.CLASSPATH, 
                            ResourceType.DRL)
            });

    private static final SimpleIbanValidator simpleIbanValidator =
            new SimpleIbanValidator();

    @Override
    public IbanValidationResult validate(String iban) {
        IbanValidationResult result = simpleIbanValidator.validate(iban);

        if (result.isValid()) {
            // The IBAN passes the basic checks. Now to get more detailed.
            IbanValidationRequest req = new IbanValidationRequest(iban);
            kenv.insert(req);
            kenv.fireAllRules();
            result.setAnnotations(req.getAnnotations());
        }

        return result;

    }

}
