RightTrack
==========

Requirements
------------

1. Home Brew:

    You can install all your dependencies with [HomeBrew](http://mxcl.github.io/homebrew/) on a Mac

2. SBT

        brew install sbt

3. (Recommended) [IntelliJ IDEA](http://www.jetbrains.com/idea/download/) Community Edition or Ultimate:


Installation
------------

From within the project directory, you can generate your project files for

    $ sbt gen-idea

This will download all dependencies of the projects and any associated sources, then link them to an idea module.
Now you just open IntelliJ IDEA and select the project directory, and it should boot up with all the required libraries.

You will need to download the Scala plugin as well as the two Play framework plugins for IDEA.

That's pretty much it.


Running the Project
-------------------

To continuously test the application:

    $ play '~test'

To run the application (compiles Scala and CoffeeScript automatically):

    $ play run


Cheers!