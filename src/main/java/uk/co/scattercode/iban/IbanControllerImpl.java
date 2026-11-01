package uk.co.scattercode.iban;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller("ibanController")
public class IbanControllerImpl implements IbanController {

    private static final Logger log = LoggerFactory.getLogger(IbanControllerImpl.class);

    /**
     * There are two validators, so the one this controller uses is named
     * rather than either being made the default.
     */
    private final IbanValidator validator;

    public IbanControllerImpl(@Qualifier("simpleIbanValidator") IbanValidator validator) {
        this.validator = validator;
    }

    @Override
    @GetMapping(value = "/iban/validator", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView validator() {
        return new ModelAndView("iban/validator");
    }

    @Override
    @GetMapping(value = "/iban/validate.json", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public IbanValidationResult validate(@RequestParam("iban") String iban) {
        IbanValidationResult result = validator.validate(iban);
        log.debug("Validating IBAN: " + result);
        return result;
    }

    public IbanValidator getValidator() {
        return validator;
    }
}
