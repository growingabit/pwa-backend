# Contributing

[code-of-conduct]: CODE_OF_CONDUCT.md

Hi there! We're thrilled that you'd like to contribute to this project. Your help is essential for keeping it great.

Please note that this project is released with a [Contributor Code of Conduct][code-of-conduct]. By participating in this project you agree to abide by its terms.

## Contribution Agreement

As a contributor, you represent that the code you submit is your original work or that of your employer (in which case you represent you have the right to bind your employer). By submitting code, you (and, if applicable, your employer) are licensing the submitted code to the open source community subject to the [GNU AGPL-3.0](LICENSE)

## Coding conventions

Please respect our code style.
We use a sligtly modified version of Google Java Style Guide. You can find useful files to configure your IDE or to chek your code through Checkstyle into the resources directory.

Also, please carefully follow the whitespace and formatting conventions already present:

- use spaces, not tabs;
- use Unix (LF), not DOS (CRLF) line endings;
- eliminate all trailing whitespace;
- preserve existing formatting; i.e. do not reformat code for its own sake;
- use UTF-8 encoding for sources files.

## Submitting changes

Please send a pull request with a clear list of what you've done. And don't forget to write test for your code: we usually like to improve our code coverage.

Please always write a clear log message for your commits. One-line messages are fine for small changes, but bigger changes should look like this:

    $ git commit -m "A brief summary of the commit
    >
    > A paragraph describing what changed and its impact."

## Coding flow

When contributing to this repository, please first discuss the change you wish to make via issue before making a change. After that, you should:

1. [fork][fork] and clone the repository;
1. create a new branch naming it `XYZ-something` where `XYZ` is the number of the issue;
1. make your changes;
1. increase the version numbers into the pom.xml file. The versioning scheme we use is [SemVer](http://semver.org/);
1. update the [CHANGELOG](CHANGELOG.md) file with details of changes.
1. run the unit tests and make sure they pass;
1. push to your fork and submit a pull request. Please attach to the pr any useful informations needed for deploy process (as, for example, new properties to configure to production);
1. relax and wait for your pull request to be reviewed and merged.

Here are a few things you can do that will increase the likelihood of your pull request being accepted:

- keep your change as focused as possible. If there are multiple changes you would like to make that are not dependent upon each other, consider submitting them as separate pull requests;
- write a good commit message;
- in your pull request description, provide as much detail as possible. This context helps the reviewer to understand the motivation for and impact of the change;
- make sure that all the unit tests still pass. PRs with failing tests won't be merged.
