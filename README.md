# RightTrack

## Requirements

1. Home Brew:

    You can install all your dependencies with [HomeBrew](http://mxcl.github.io/homebrew/) on a Mac

2. Scala: Simple Build Tool (SBT)

        $ brew install sbt

3. JavaScript: Node Package Manager (NPM)

        $ brew install npm

4. (Recommended) [IntelliJ IDEA](http://www.jetbrains.com/idea/download/) Community Edition or Ultimate:


## Installation

### Play Framework and API Dependencies

To download and install the dependencies for the API, just run:

    $ sbt update  # fetch updates from ivy / maven repositories

### Grunt and Bower Packages

    $ npm install  # install grunt and node modules
    $ npm install -g bower  # install bower for web modules
    $ bower install  # install bower packages locally

## IntelliJ IDEA / Scala IDE

From within the project directory, you can generate your project files with

    $ sbt gen-idea  # generate libraries for IntelliJ IDEA

This will download all dependencies of the projects and any associated sources, then link them to an idea module.
Now you just open IntelliJ IDEA and select the project directory, and it should boot up with all the required libraries.

You will need to download the Scala plugin as well as the two Play framework plugins for IDEA.

For Scala IDE, you can generate your project files with

    $ sbt eclipse


##  Running the Project

To run api unit tests on API source code changes:

    $ play '~test'

To run the api server (compiles and redeploys source changes automatically):

    $ play run

To run Jasmine unit tests with PhantomJS on CoffeeScript source code changes:

    $ grunt test

To continuously deploy source-mapped CoffeeScript on source code changes:

    $ grunt work

To deploy concatonated, uglified, and source-mapped JavaScript in a production environment:

    $ grunt release


## Thanks

If any of these instructions are out of date, please update them here.

Cheers!