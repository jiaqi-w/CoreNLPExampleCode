Download Stanford CoreNLP NLP Toolkit
Here are two links for the online demo, they are basically the same.
http://nlp.stanford.edu:8080/corenlp/
http://corenlp.run/

If you would like to use Stanford toolkits for dependency parsing or sentiment analysis, please follow the instruction below:
1. Download Stanford CoreNLP 3.6:
http://stanfordnlp.github.io/CoreNLP/download.html
Unzip/tar the package, you will have the decompress folder, such as stanford-corenlp/. 

2. Download Java8 JDK ( Java SE Development Kit 8u73), click the ¡°Accept License Agreement¡±, then pick the right one to download for your working environment.
http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

3. Install Java8 JDK:
The installation documentation for Windows:
http://docs.oracle.com/javase/8/docs/technotes/guides/install/windows_jdk_install.html#CHDEBCCJ
The installation documentation for Mac:
http://docs.oracle.com/javase/8/docs/technotes/guides/install/mac_jdk.html#CHDBADCG
The installation documentation for Linux:
http://docs.oracle.com/javase/8/docs/technotes/guides/install/linux_jdk.html#BJFGGEFG

4. To run Stanford toolkits by command line, refer to http://stanfordnlp.github.io/CoreNLP/cmdline.html
Remember the decompress folder name in Step 1 if you would use specific classpath directory.

5. Ready Go!
Command line for dependency:
http://nlp.stanford.edu/software/nndep.shtml
Command line for sentiment analysis:
http://nlp.stanford.edu/sentiment/code.html
Parse Dependency:
./__parse.sh ~/stanford-corenlp data/ data-parsed.txt
Retrain Sentiment Model:
./__retrain.sh ~/stanford-corenlp retrain-model