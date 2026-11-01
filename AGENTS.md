# AGENTS.md

Guidance for anyone working in this repository with a coding agent (Claude
Code, Codex, Cursor, Gemini CLI or whatever else reads this file) and a fair
summary of the house rules for contributors who use none.

## What this repository is

**sctrcd-fx-web** is a small Spring web application that validates IBANs,
with some of the validation done by the Drools rules engine. It is a teaching
example, not a library or a product: nothing is released or deployed, and
the code should stay as short as the point it makes. The blog post
[A web service powered by Spring and Drools](https://scattercode.dev/2013/01/a-web-service-powered-by-spring-and-drools/)
links here.

The point is this. Wiring Drools into a Spring application needs no special
integration library: a service builds its own knowledge base from DRL on the
classpath (`DroolsUtil.createKnowledgeBase`), and opens a session for each
request. `RuleBasedIbanValidator` does that with a stateless session, and the
rule tests show the rule firing, or not, for a given IBAN.

## Hard rules

### Keep the pages and endpoints

`/`, `/iban/validator` and `/iban/validate.json` are what the blog post and
the original application described. Keep their paths and the JSON shape of
`IbanValidationResult`; static files stay under `/resources/**`.

### Name the validator, never make one the default

There are two `IbanValidator` beans. `IbanControllerImpl` asks for
`simpleIbanValidator` by name with `@Qualifier`. Do not mark either one
`@Primary`: that would hide which validator the endpoint uses.

### Wire Drools by hand

The knowledge base is built in code from `DroolsResource`s, which is what the
demo demonstrates. Do not replace it with `kmodule.xml` discovery, the KIE
Spring integration or the KIE Maven plugin. Change a `.drl` file only when
Drools itself requires it, and keep the rule names, which the tests assert.

### No CDN scripts

The pages use plain JavaScript served from `src/main/resources/static`. Do
not add script tags that load from a CDN.

### Stay on current LTS versions

The project tracks the latest LTS JDK (`java.version` in `pom.xml`, and
`java-version` in the workflows; change them together), the latest Spring
Boot GA release and the latest Apache KIE Drools release (`drools.version`).
Dependency versions come from the Spring Boot parent's dependency
management where it has one: do not pin a version that the parent already
manages.

## Layout

| Path | What |
|---|---|
| `src/main/java/.../FxWebApplication.java` | `@SpringBootApplication`; scans `uk.co.scattercode` |
| `src/main/java/.../web/WebConfig.java` | The `/` view, and the locale resolver and interceptor |
| `src/main/java/.../iban/` | The controller, both validators, the MOD-97 check and the facts the rules use |
| `src/main/java/.../drools/util/` | The hand-wired Drools helpers: building a knowledge base, environments, tracking listeners |
| `src/main/java/.../fx/facts/` | FX payment facts for rules yet to be written |
| `src/main/resources/rules/` | The DRL |
| `src/main/resources/templates/` | Thymeleaf pages |
| `src/main/resources/static/` | CSS and JavaScript, served at `/resources/**` |
| `src/test/java/.../iban/` | Validator, rule and MockMvc endpoint tests |
| `newMockup.bmml` | A Balsamiq mock-up of the page |
| `.githooks/commit-msg` | Conventional Commits check, shared from `scattercode/release-pipelines` |

## Checks

```bash
./mvnw spotless:apply   # format the Java sources and the POM
./mvnw verify           # everything below, and the tests
```

`./mvnw verify` runs, in order:

- the **enforcer**: the JDK is at least `java.version`, Maven is at least
  3.9, and dependency versions converge;
- the **Spotless** check (palantir-java-format for Java, sortpom for
  `pom.xml`) in the `validate` phase;
- the **compiler** with `-Xlint:all -Werror`, so any warning fails the build;
- the **tests**, and the executable jar.

It needs JDK 25; the Maven wrapper fetches Maven itself. All of it must
pass before a commit. The lefthook hooks run the format check before each
commit and the full build before each push; CI runs the build and a Trivy
scan on every push and pull request.

## Conventions

- British English; sentence case for headings.
- Conventional Commits, enforced by `.githooks/commit-msg` and, for pull
  request titles, by `pr-title.yml`. Dependency updates are `build:`,
  workflow changes `ci:`.
- `.githooks/commit-msg` is a byte-for-byte copy of the template in
  `scattercode/release-pipelines`; CI checks it has not drifted. Change it
  there, not here.
- `main` is protected: changes arrive through pull requests with the CI
  checks passing.
- Hook setup in a fresh clone: `lefthook install`, or without lefthook,
  `git config core.hooksPath .githooks` for the commit message check alone.
