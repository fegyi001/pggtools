# pggtools
> A few Java tools mainly for webGIS stuff

## Contents
* <b>MapfishPrintTools</b>: if you ever suffered from generating PDFs using MapFish's print-servlet maybe this can help you. The requests stay behind the server, therefore no cross-domain restrictions will arise. You will also recieve (error) messages in a nicer way.
* more stuff will come

## Usage
This is a maven project. To build, simply run ```mvn clean install -Dmaven.test.skip=true``` from the command line. After that, you can use it as a maven dependency in you own project. 

## Examples
Please explore ```src/test/java``` for test and example classes.

## Author
```pggtools``` was created by [Gergely Padányi-Gulyás](http://www.gpadanyig.com)