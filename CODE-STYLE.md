# Code Style

Describes various code styling rules for HippOutLocalizationLib. These should be followed whenever possible when
contributing to the repository.

# General

- Wrap at column 115. Overrunning by a couple characters is fine.
- Import with wildcards when using more than one class in a package.
- Performant code is important, but don't go insane. Don't bother with fast code for things that only run once, i.e. at
  server startup.
- Fail early. Throw an exception instead of trying to continue silently in most cases. Correcting plugins' mistakes is
  the job of the developer, not the API.
- Don't use braces when they aren't necessary. They clutter things. For example, a one-line if and a one-line function
  should not use braces. For another example, a loop around such an if-statement should use braces, but the if-statement
  inside need not.

If you have more questions, ask in your pull request.