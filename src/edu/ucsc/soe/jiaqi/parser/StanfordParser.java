package edu.ucsc.soe.jiaqi.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class StanfordParser {
	
	public static final StanfordCoreNLP pipeline = initalPipeline();

	public StanfordParser() {
	}

	public static StanfordCoreNLP initalPipeline() {
		Properties props = new Properties();
		props.setProperty("annotators",
				"tokenize,ssplit,pos,lemma,ner,parse,depparse,sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		return pipeline;
	}
	
	public static void parseFile(Path inPath, Path outFilePath) {
		System.out.println("Input: " + inPath);
		System.out.println("Output: " + outFilePath);
		try (BufferedWriter writer = Files.newBufferedWriter(outFilePath)) {
			try (Stream<Path> paths = Files.walk(inPath)) {
				paths.filter(path -> !Files.isDirectory(path))
				     .forEach(inFilePath -> saveFile(inFilePath, writer));
			}
			System.out.println("Parsed Text Saved in: " + outFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void saveFile(Path inFilePath, BufferedWriter writer) {
		try (BufferedReader reader = Files.newBufferedReader(inFilePath)) {
			ArrayList<String> lines = reader.lines().filter(line -> !line.isEmpty())
					.collect(Collectors.toCollection(ArrayList<String>::new));
			writer.write("-------------------------------" + System.lineSeparator());
			writer.write("Read from file: " + inFilePath + System.lineSeparator() + "Total number of lines is " + lines.size()
					 + System.lineSeparator() + System.lineSeparator());
			for (int i = 0; i < lines.size(); i++) {
				writer.write("Read from line " + (i + 1) + ": ");
				writer.write(parseText(lines.get(i)));
				writer.write(System.lineSeparator());
			}
			writer.write("-------------------------------" + System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String parseText(String text) {
		Annotation annotation = pipeline.process(text);
		List<CoreMap> sentences = annotation
				.get(CoreAnnotations.SentencesAnnotation.class);
		
		if (sentences == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer("The total number of sentences in this line is: " + sentences.size() + System.lineSeparator() + System.lineSeparator());
		for (int i = 0; i < sentences.size(); i++) {
			CoreMap sentence = sentences.get(i);
			sb.append("Sentence " + (i + 1) + ": ");
//			sb.append(getGeneralInfo(sentence) + System.lineSeparator());
			sb.append(sentence.toString() + System.lineSeparator() + System.lineSeparator());
			sb.append(getSentiment(sentence) + System.lineSeparator() + System.lineSeparator());
			sb.append(getParseTree(sentence) + System.lineSeparator() + System.lineSeparator());
			sb.append(getBasicDependencies(sentence) + System.lineSeparator());
			sb.append(getCollapsedDependencies(sentence) + System.lineSeparator());
		}
		return sb.toString();
	}
	
	private static String getParseTree(CoreMap sentence) {
		Tree tree = sentence
				.get(TreeCoreAnnotations.TreeAnnotation.class);
		return "parse tree: " + System.lineSeparator() + tree.toString();
	}
	
	private static String getSentiment(CoreMap sentence) {
		// Get sentiment, such as "Negative", "Very Negative", "Neutral", etc.
		String sentiment = sentence
				.get(SentimentCoreAnnotations.SentimentClass.class);
		return "sentiment results: " + sentiment;
	}
	
	private static String getBasicDependencies(CoreMap sentence) {
		String dependency = sentence
		.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class)
		.toString(SemanticGraph.OutputFormat.LIST);
		return "basic dependencies: " + System.lineSeparator() + dependency;
	}
	
	private static String getCollapsedDependencies(CoreMap sentence) {
		// http://nlp.stanford.edu/software/stanford-dependencies.shtml
		SemanticGraph graph = sentence
				.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
		String dependency = graph.toString(SemanticGraph.OutputFormat.LIST);
		return "collapsed, CC-processed dependencies: " + System.lineSeparator() + dependency;
	}
	
	@SuppressWarnings("unused")
	private static String getGeneralInfo(CoreMap sentence) {
		// This function will return the most general Stanford information of sentence.
		return "sentence:" + System.lineSeparator() + sentence.toShorterString();
	}
	
	public static void main(String[] args) throws IOException {
		// One argument
		// Text:
		// java -Xmx2G -cp "*" StanfordParser "Anna is reading."
		// Text file:
		// java -Xmx2G -cp "*" StanfordParser ~/test/text.txt
		// Text directory:
		// java -Xmx2G -cp "*" StanfordParser ~/test
		
		// Two arguments for both input and output
		// java -Xmx2G -cp "*" StanfordParser ~/test ~/test_parsed.txt
		
		if (args.length == 0) {
			// Test Mode
			System.out.println();
			System.out.println(parseText("Anna is reading a book. She pause and look out of the window."));
		
		} else if (args.length == 1) {
			// Only the one argument is given.
			// Note that the single argument can be the directory of text files or the exact text file or the exact text with quote.
			Path inPath = Paths.get(args[0]);
			if (Files.exists(inPath)) {
				String inFileName = inPath.getFileName().toString();
				String outFileName = null;
				if (inFileName.lastIndexOf(".") >= 0) {
					outFileName = inFileName.substring(0, inFileName.lastIndexOf(".")) + "_parsed.txt";
				} else {
					outFileName = inFileName + "_parsed.txt";
				}
				// The output file is saved outside the parent directory. 
				parseFile(inPath, Paths.get(inPath.getParent().toString(), outFileName));
				
			} else {
				System.out.println();
				System.out.println(parseText(args[0]));
			}
			
		} else {
			// Both input and output name are specified.
			parseFile(Paths.get(args[0]), Paths.get(args[1]));
			
		}
	}
}
