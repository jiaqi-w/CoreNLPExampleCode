# $1: uncompress package of stanford-corenlp.
# $2: input can be a directory of text files, a text file, or a plain text.
# $3: output file name which combine all the results.
# Example:
# ./__parse.sh ~/stanford-corenlp data/ data-parsed.txt
# ----------------------------------------
lib=$1
CLASS_PATH=${lib}/stanford-corenlp-3.7.0.jar:${lib}/stanford-corenlp-3.7.0-models.jar:${lib}/stanford-corenlp-3.7.0-javadoc.jar:${lib}/ejml-0.23.jar:${lib}/slf4j-api.jar:${lib}/slf4j-simple.jar:${lib}/joda-time.jar:${lib}/jollyday.jar
#echo ${CLASS_PATH}

input=$2
output=$3

# Update/recompile the StanfordParser.java
javac -d bin -sourcepath src -cp ${CLASS_PATH} src/main/java/edu/ucsc/soe/nlp/parser/StanfordParser.java

echo "Parsing ... "
java -cp bin:${CLASS_PATH} -Xmx2g main.java.edu.ucsc.soe.nlp.parser/StanfordParser ${input} ${output}  2>/dev/null
