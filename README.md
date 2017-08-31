# pwa-backend

[![Build Status](https://travis-ci.org/growingabit/pwa-backend.svg?branch=master)](https://travis-ci.org/growingabit/pwa-backend)
[![codecov](https://codecov.io/gh/growingabit/pwa-backend/branch/master/graph/badge.svg)](https://codecov.io/gh/growingabit/pwa-backend)
[![Code Climate](https://codeclimate.com/github/growingabit/pwa-backend/badges/gpa.svg)](https://codeclimate.com/github/growingabit/pwa-backend)
[![Issue Count](https://codeclimate.com/github/growingabit/pwa-backend/badges/issue_count.svg)](https://codeclimate.com/github/growingabit/pwa-backend)

## Contribution
Read the [CONTRIBUTING](CONTRIBUTING.md) file.

## Changelog
[CHANGELOG](CHANGELOG.md).

## Run the application

`mvn appengine:run`

## Executing tests

### Unit tests
`mvn clean verify`

### Integration tests
`mvn clean verify -P integration-tests`

### All tests
`mvn clean verify -P all-tests`

## Other useful maven commands:

- `mvn checkstyle:check` to perform a Checkstyle analysis and print violations to the console;
- `mvn versions:display-dependency-updates` to check for update of dependencies;
- `mvn versions:display-plugin-updates` to check for update of plugins.
