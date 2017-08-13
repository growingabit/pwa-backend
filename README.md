# pwa-backend

[![Build Status](https://travis-ci.org/growingabit/pwa-backend.svg?branch=master)](https://travis-ci.org/growingabit/pwa-backend)
[![codecov](https://codecov.io/gh/growingabit/pwa-backend/branch/master/graph/badge.svg)](https://codecov.io/gh/growingabit/pwa-backend)
[![Code Climate](https://codeclimate.com/github/growingabit/pwa-backend/badges/gpa.svg)](https://codeclimate.com/github/growingabit/pwa-backend)
[![Issue Count](https://codeclimate.com/github/growingabit/pwa-backend/badges/issue_count.svg)](https://codeclimate.com/github/growingabit/pwa-backend)

## Contribution
Please respect our code style.
We use a sligtly modified version of Google Java Style Guide. You can find useful files to configure your ide or to make code analisys through checkstyle into the resources directory

## useful maven commands:

- `mvn test` to run test (obv :D);
- `mvn cobertura:cobertura` to run coverage tool. You can see a coverage report navigating into `target/site/cobertura`;
- `mvn checkstyle:check` to perform a Checkstyle analysis and print violations to the console;
- `mvn versions:display-dependency-updates` to check for update of dependencies
- `mvn versions:display-plugin-updates` to check for update of plugins
