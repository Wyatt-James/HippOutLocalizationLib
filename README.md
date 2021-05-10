# HippOutLocalizationLib

A Bukkit Plugin API to make localization easy for plugin developers, programmed for Java 8.

## Installation

Download the [latest release](https://github.com/Wyatt-James/HippOutLocalizationLib/releases/latest) from the releases
tab and place HippOutTranslationLib.jar into your Bukkit Server's `Plugins` folder. You must also remove any previous
versions.

## Compiling

1. install [Apache Maven](https://maven.apache.org/) and [Java JDK 8 or greater](https://adoptopenjdk.net/).
2. run the command `mvn package` from the root project directory to compile a JAR. To also install to your local maven
   repository, run `mvn install` instead. You do not have to run both.

For installation, refer to the Installation section.

## Contributing

Pull requests are welcome from licensed developers (effectively HippOut employees). For major changes, please open an
issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

This project follows [Semantic Versioning 2.0.0.](https://semver.org/)

Extra developer notes:

- Wrap at column 105. Overrunning by a couple characters is fine.
- Import with wildcards when using more than one class in a package.
- Performant code is important, but don't go insane. Don't bother with fast code for things that only run once, i.e. at
  server startup.
- Fail early. Exceptions are all the rage these days, aren't they?
- Don't squash commits coz high numbers are fun. However, only one version increment per pull request.
- More style guidelines coming soon to a github repo near you!

## License

HippOutLocalizationLib does not currently have a license and therefore falls under exclusive Copyright. A license may be
added in the future.