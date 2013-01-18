package uk.co.scattercode.iban;

import org.springframework.web.servlet.ModelAndView;


public interface IbanController {

    ModelAndView validator();

	IbanValidationResult validate(String iban);

}
