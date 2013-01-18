package uk.co.scattercode.iban;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.servlet.ModelAndView;

import uk.co.scattercode.iban.IbanController;
import uk.co.scattercode.iban.IbanControllerImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { IbanConfig.class }, loader = AnnotationConfigContextLoader.class)
public class IbanControllerTest {

    IbanController controller;

    @Before
    public void before() {
        IbanControllerImpl controller = new IbanControllerImpl();
        controller.setValidator(new SimpleIbanValidator());
        this.controller = controller;
    }

    @Test
    public void shouldProvideForm() {
        ModelAndView mav = controller.validator();
        assertEquals("iban/validator", mav.getViewName());
    }

    @Test
    public void shouldValidate() {
        String iban = "ES23 0217 0099 47";
        IbanValidationResult result = controller.validate(iban);
        assertNotNull(result);
    }

}
