package com.comments.test;

import java.util.List;

import org.junit.Test;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class NLPTest {

	public static void main(String args[]){

		Sentence sentence = new Sentence("Lucy is in the sky with diamonds.");
//		List<String> nerTags = sentence.nerTags();
//		for(String ner:nerTags){
//			System.out.println(ner);
//		}
		List<String> mentions = sentence.mentions();
		for(String mention:mentions){
			System.out.println(mention);
		}
//		Document doc = new Document("Lucy is in the sky with diamonds.");
//        for (Sentence sent : doc.sentences()) {  // Will iterate over two sentences
//            // We're only asking for words -- no need to load any models yet
//            System.out.println("The second word of the sentence '" + sent + "' is " + sent.word(1));
//            // When we ask for the lemma, it will load and run the part of speech tagger
//            System.out.println("The third lemma of the sentence '" + sent + "' is " + sent.lemma(2));
//            // When we ask for the parse, it will load and run the parser
//            System.out.println("The parse of the sentence '" + sent + "' is " + sent.parse());
//            // ...
//        }
	}
	
	@Test
	public void test(){
//		String path = "./data/ace2004/RawTexts/chtb_165.eng";
		String content = "质量很好，是纯棉的，物流很快，很喜欢，款式很好看。喜欢的亲就购买吧？";
		// build pipeline
		
		StanfordCoreNLP pipeline = new StanfordCoreNLP("CoreNLP-chinese.properties");
		Annotation document = new Annotation(content);
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

		for(CoreMap sentence: sentences) {
			  // traversing the words in the current sentence
			  // a CoreLabel is a CoreMap with additional token-specific methods
			  for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
			    // this is the text of the token
			    String word = token.get(TextAnnotation.class);
			    // this is the POS tag of the token
			    String pos = token.get(PartOfSpeechAnnotation.class);
			    // this is the NER label of the token
			    String ne = token.get(NamedEntityTagAnnotation.class);
			    System.out.println(word+"\t"+pos+"\t"+ne);
			  }

			  // this is the parse tree of the current sentence
			  Tree tree = sentence.get(TreeAnnotation.class);
			  System.out.println(tree);
			  // this is the Stanford dependency graph of the current sentence
			  SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			  System.out.println(dependencies);
			  System.out.println(dependencies.toList());
			  System.out.println(dependencies.toPOSList());
		}
	}
}
