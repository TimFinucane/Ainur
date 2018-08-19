<div align="center">
  <img src="https://github.com/TimFinucane/Ainur/blob/master/docs/images/logo.jpg"><br><br>
</div>

-----------------


| **`Build`** |
|-----------------|
| [![Build Status](https://tim-finucane.com/jenkins/job/ainur/job/Ainur/job/master/badge/icon)](https://tim-finucane.com/jenkins/job/ainur/job/Ainur/job/master/) |

**Ainur** is a solution for the multiprocessor scheduling problem, inspired by the tale of the Lord of the Rings.

## Getting started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See sections 2.0 in the Ainur wiki for more detailed information on how to run it.

### Prerequisites
- Java 8 exactly needs to be installed.
- Oracle JDK needs to be installed.

### Building
Clone repository and navigate to the top parent folder.
 Ainur uses Gradle to build and project comes with the Gradle wrapper, so gradle is not essential to install before hand. Using a terminal, use one of the two commands below to build it.

```
gradle build
```
```
./gradlew
```

## Running the Tests
Use command below to run all Gradle tests. These involve basic unit tests as well as full integration tests using over 400 sample inputs. 

Note: The integration tests will take longer than conventional unit tests.

```
gradle test
```

## Usage
You can run Ainur from command line/terminal. All that is needed is the Ainur jar file and a graph file of the graph the you want to run, in .dot format. (see (dot file specifications)[https://www.graphviz.org/doc/info/lang.html] for more information)

From a terminal, use the below code snippet to run the application.
```
java -jar Ainur.jar INPUT.dot P [OPTIONS]
```
For more detailed usage documents, see [Ainur wiki section 2.0](https://github.com/TimFinucane/Ainur/wiki/Command-Line-Interface_)

## Documentation
[Ainur Wiki](https://github.com/TimFinucane/Ainur/wiki) gives detailed information for developers and users who would like to interact with the Ainur application.
[Data](https://github.com/TimFinucane/Ainur/tree/master/data) contains all sample data graphs in .dot format provided by Oliver Sinnen and other sources.
[Docs](https://github.com/TimFinucane/Ainur/tree/master/docs) folder contains all design notes, UML diagrams, images and meeting minutes that were made throughout the duration of the project. Also contains a list of references on different papers and designs that helped make Ainur.

Conversations to do with issues and features are located on the [Ainur issues page](https://github.com/TimFinucane/Ainur/issues)

## Contributors
The following people contributed to the program, there upis are also given for uni reference.

| Contributor | UPI | GitHub
| ----------- | --- | ----- |
| Maddie Beagley - Team Leader | mbea966 | https://github.com/maddiebeagley
| Nathan Cairns | ncai762 | https://github.com/Nathan-Cairns
| Buster Darragh-Major | bmaj406 | https://github.com/Buster-Darragh-Major
| Tim Finucane | tfin440 | https://github.com/TimFinucane
| Emilie Pearce | epea390 | https://github.com/emipeanz

## Licence
This project is licenced under [The GNU General Public License v3.0](https://github.com/TimFinucane/Ainur/blob/master/LICENSE.md). This licence only applies to code we have written, not the data.

## Acknowledgements
- Oliver Sinnen for use of his previous papers and research on the topic
- Gandalf from Lord of the Rings for his inspiration in our projects name and group title

## References 
- Graphs for the test suite were obtained from http://homepages.engineering.auckland.ac.nz/~parallel/OptimalTaskScheduling/OptimalSchedules.html

## Copyright notices
We do not own any rights and are not associated with Lord of the Rings in any way, shape or form. The use of titles and names are a parody and are used meerly for our own entertainment. Logos and images were made as a take-off of Lord of the Rings and are not associated with official merchandise or media.
