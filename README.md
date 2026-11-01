sctrcd-fx-web
=============

This project is a Java web application based on Spring and Drools. The intent is to provide
simple examples of using Drools within a web application, with the integration wired by hand
rather than through the KIE Spring integration.

It validates IBANs. There are two validators: `SimpleIbanValidator` checks the country code and
the MOD-97 checksum in Java, and `RuleBasedIbanValidator` performs the MOD-97 check and then
runs the request through the rules in
[`IbanRules.drl`](src/main/resources/rules/sctrcd/iban/IbanRules.drl) in a stateless Drools
session. The web endpoints use the simple validator.

| Path | What |
|---|---|
| `/` | The welcome page |
| `/iban/validator` | A form that validates an IBAN through the JSON service |
| `/iban/validate.json?iban=…` | The validation result as JSON |

It needs Java 25 and uses Spring Boot 4 and Drools 10. The Maven wrapper downloads Maven
itself, so to run a full build, including the format check and the tests:

    ./mvnw clean verify

To run the web application on <http://localhost:9090/>:

    ./mvnw spring-boot:run

or, after a build, `java -jar target/sctrcd-fx-web-1.0.0-SNAPSHOT.jar`.

If the format check fails, `./mvnw spotless:apply` fixes it. To install the git hooks, which
check formatting before each commit and run the build before each push:

    lefthook install

See [AGENTS.md](AGENTS.md) for the conventions and [SECURITY.md](SECURITY.md) for reporting a
vulnerability.

Licensed under the [MIT License](LICENSE): copy and adapt it freely.
