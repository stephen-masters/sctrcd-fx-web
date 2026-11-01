package uk.co.scattercode.iban;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

/**
 * Each endpoint of the web application, through the whole Spring context.
 */
@SpringBootTest
@AutoConfigureMockMvc
class IbanControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    IbanControllerImpl controller;

    @Test
    void shouldUseTheSimpleValidator() {
        assertThat(controller.getValidator()).isInstanceOf(SimpleIbanValidator.class);
    }

    @Test
    void shouldShowTheWelcomePage() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"))
                .andExpect(content().string(containsString("<h1><a href=\"/\">Scattercode FX</a></h1>")))
                .andExpect(content().string(containsString("href=\"/iban/validator\"")));
    }

    @Test
    void shouldChangeTheLocaleFromARequestParameter() throws Exception {
        mvc.perform(get("/").param("locale", "de"))
                .andExpect(status().isOk())
                .andExpect(cookie().value(CookieLocaleResolver.DEFAULT_COOKIE_NAME, "de"));
    }

    @Test
    void shouldProvideTheForm() throws Exception {
        mvc.perform(get("/iban/validator").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("iban/validator"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("action=\"/iban/validate.json\"")))
                .andExpect(content().string(containsString("id=\"ibanRequest\"")))
                .andExpect(content().string(containsString("src=\"/resources/js/iban.js\"")));
    }

    @Test
    void shouldOnlyServeTheFormAsHtml() throws Exception {
        mvc.perform(get("/iban/validator").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldValidateAValidIban() throws Exception {
        mvc.perform(get("/iban/validate.json")
                        .param("iban", "GB29 NWBK-6016 1331 9268 19")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.originalForm").value("GB29 NWBK-6016 1331 9268 19"))
                .andExpect(jsonPath("$.electronicForm").value("GB29NWBK60161331926819"))
                .andExpect(jsonPath("$.displayForm").value("GB29 NWBK 6016 1331 9268 19"))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.annotations").isEmpty());
    }

    /**
     * The simple validator looks the country code up before sanitising the
     * IBAN, so a lower-case country code is not found, as it never was.
     */
    @Test
    void shouldRejectALowerCaseCountryCode() throws Exception {
        mvc.perform(get("/iban/validate.json").param("iban", "gb29 nwbk 6016 1331 9268 19"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.electronicForm").value("GB29NWBK60161331926819"))
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void shouldRejectAnInvalidIban() throws Exception {
        mvc.perform(get("/iban/validate.json").param("iban", "LU36 0029 1534 6005 0000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayForm").value("LU36 0029 1534 6005 0000"))
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void shouldRequireAnIban() throws Exception {
        mvc.perform(get("/iban/validate.json")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldOnlyServeTheResultAsJson() throws Exception {
        mvc.perform(get("/iban/validate.json")
                        .param("iban", "GB29 NWBK 6016 1331 9268 19")
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldServeStaticResources() throws Exception {
        mvc.perform(get("/resources/css/iban.css"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("validateIbanBtn")));
        mvc.perform(get("/resources/css/application.css")).andExpect(status().isOk());
        mvc.perform(get("/resources/js/iban.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("form.action")));
    }
}
