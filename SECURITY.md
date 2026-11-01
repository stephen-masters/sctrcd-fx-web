# Security policy

## Reporting a vulnerability

Please report suspected vulnerabilities privately through GitHub's
[private vulnerability reporting](https://github.com/stephen-masters/sctrcd-fx-web/security/advisories/new)
(Security tab → "Report a vulnerability"). Do not open a public issue for a
security problem.

## Supported versions

This is a demonstration project with no releases. Only `main` is maintained.

## What is done to keep it safe

- Dependency versions come from the Spring Boot parent where it manages
  them; Drools and Commons BeanUtils are pinned in `pom.xml`. Dependabot
  keeps the parent on the latest GA release and the pinned versions current,
  and also keeps the Maven wrapper and the workflow actions current.
- Trivy scans the dependencies for HIGH and CRITICAL vulnerabilities on
  every push and pull request, and weekly on a schedule, and CI publishes a
  CycloneDX SBOM.
- The application holds no credentials and stores nothing. When run, it
  listens on port 9090 with no authentication, so it is meant for a local
  machine, not a public network.
