package uk.co.scattercode.iban;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller(value="ibanController")
public class IbanControllerImpl implements IbanController {

    private static Logger log = LoggerFactory.getLogger(IbanControllerImpl.class);
    
    @Resource(name="simpleIbanValidator")
	IbanValidator validator;
	
    @Override
    @RequestMapping(value="/iban/validator", method=RequestMethod.GET, headers="Accept=text/html")
    public ModelAndView validator() {
        ModelAndView mav = new ModelAndView("iban/validator");
        return mav;
    }
	
	@Override
	@RequestMapping(value="/iban/validate.json", method=RequestMethod.GET, headers="Accept=application/json")
	public @ResponseBody IbanValidationResult validate(String iban) {
	    IbanValidationResult result = validator.validate(iban);
        log.debug("Validating IBAN: " + result.toString());
        return result;
	}

    public IbanValidator getValidator() {
        return validator;
    }

    public void setValidator(IbanValidator validator) {
        this.validator = validator;
    }
	
}
