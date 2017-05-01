package com.comments.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class NLPUtils {
	
	private static StanfordCoreNLP pipeline = new StanfordCoreNLP("CoreNLP-chinese.properties");
	//用于匹配依存二元关系，如nsubj(拉-10, 宝贝-8)
	private static Pattern pattern = Pattern.compile(".*?\\((.*?)-(.*?), (.*?)-(.*?)\\)");
	public static void main(String args[]){
		
		String rpath = "./commentsData/cleanData/comments.txt";
		String wpath = "./commentsData/candidateLabels.txt";
		candidateLabels(rpath, wpath);
		
//		String rpath = "./commentsData/trainData/trainComments.txt";
//		String wpath = "./commentsData/trainData/segTrainComments.txt";
//		String stopDictPath = "./dict/stopwords.txt";
//		segment(rpath, wpath,stopDictPath);
	}
	
	public static void segment(String rpath,String wpath,String stopDictPath){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(rpath)), "utf-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(wpath)), "utf-8"));
			String line;
			StringBuilder builder = new StringBuilder();
			Set<String> stopDict = CommonUtils.loadSet(stopDictPath);
			int i = 0;
			while((line = reader.readLine())!=null){
				String[] lineArray = line.split("[，。！?]");
				if(lineArray==null || lineArray.length<=0)
					continue;
				builder.delete(0, builder.length());
				System.out.println(i++);
				System.out.println(line);
				for(String subLine:lineArray){
					Annotation document = new Annotation(subLine);
					try {
						pipeline.annotate(document);
					} catch (Exception e) {
						// TODO: handle exception
						continue;
					}
					
					List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
					for(CoreMap sentence: sentences) {
						 for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
						    // this is the text of the token
						    String word = token.get(TextAnnotation.class);
						    if(!stopDict.contains(word)){
						    	builder.append(word).append(" ");
						    }
						 }
					}
					
				}
				builder.append("\n");
				System.out.println(builder.toString());
				writer.write(builder.toString());
				writer.flush();
			}
			
			reader.close();
			writer.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 输出所有的候选标签
	 * @param rpath
	 * @param wpath
	 */
	public static void candidateLabels(String rpath,String wpath){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(rpath)), "utf-8"));
			String line;
			while((line = reader.readLine()) != null){
				String[] lineArray = line.split("[，。！?]");
				if(lineArray==null || lineArray.length<=0)
					continue;
				for(String subLine:lineArray){
					if(subLine == null || subLine.equals(""))
						continue;
					Annotation document = new Annotation(subLine);
					try {
						pipeline.annotate(document);
					} catch (Exception e) {
						// TODO: handle exception
						continue;
					}
					List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
					for(CoreMap sentence: sentences) {
						  // this is the Stanford dependency graph of the current sentence
						  SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
						  getCandidateLabels(dependencies.toList(), wpath);
					}
				}
				
			}
			reader.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
		}
		
	}
	
	/**
	 * 获取每句评论中的候选标签
	 * @param content
	 * @return
	 */
	public static void getCandidateLabels(String content,String wpath){
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(wpath), true), "utf-8"));
			List<String> labelList = extractLabels(content);
			for(String str:labelList){
			    writer.write(str+"\n");
			}
			writer.close();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static List<String> extractLabels(String dependencyComments){
		String[] dependencyArray = dependencyComments.split("\n");
		int len = dependencyArray.length;
		List<String> labelList = new ArrayList<>();
		
		for(int i=0;i<len;i++){			
			if(dependencyArray[i].contains("nsubj")){
				//处理主副修饰词与主修饰词的情况
				if(i<len-2 && dependencyArray[i+1].contains("advmod") && dependencyArray[i+2].contains("advmod")){
					//1.处理nsubj + advmod + advmod
					Set<String> wordsSet = new HashSet<>();//words用于判断该词是否已经添加进去
					List<String[]>wordsList = new ArrayList<>();
					for(int j=0;j<=2;j++){
						List<String[]>tempList = getWordsUnit(dependencyArray[i+j]);
						if(tempList!=null){
							for(int k=0;k<2;k++){
								String item = tempList.get(k)[0];
								if(!wordsSet.contains(item)){
									wordsList.add(tempList.get(k));
									wordsSet.add(item);
								}
							}
							
						}
						
					}
					if(wordsList.size()>0){
						sortList(wordsList);
						String ss = joinList(wordsList);
						labelList.add(ss);
					}
					i+=2;
				}else if(i<len-1 && dependencyArray[i+1].contains("advmod")){
					//2.处理nsubj + advmod
					Set<String> wordsSet = new HashSet<>();//words用于判断该词是否已经添加进去
					List<String[]>wordsList = new ArrayList<>();
					for(int j=0;j<=1;j++){
						List<String[]>tempList = getWordsUnit(dependencyArray[i+j]);
						if(tempList!=null){
							for(int k=0;k<2;k++){
								String item = tempList.get(k)[0];
								if(!wordsSet.contains(item)){
									wordsList.add(tempList.get(k));
									wordsSet.add(item);
								}
							}
							
						}
						
					}
					if(wordsList.size()>0){
						sortList(wordsList);
						String ss = joinList(wordsList);
						labelList.add(ss);
					}
					i++;
				}
			}else if(dependencyArray[i].contains("advmod")){
				if(i<len-1 && dependencyArray[i+1].contains("advmod")){
					//3.处理advmod + advmod
					Set<String> wordsSet = new HashSet<>();//words用于判断该词是否已经添加进去
					List<String[]>wordsList = new ArrayList<>();
					for(int j=0;j<=1;j++){
						List<String[]>tempList = getWordsUnit(dependencyArray[i+j]);
						if(tempList!=null){
							for(int k=0;k<2;k++){
								String item = tempList.get(k)[0];
								if(!wordsSet.contains(item)){
									wordsList.add(tempList.get(k));
									wordsSet.add(item);
								}
							}
							
						}
						
					}
					if(wordsList.size()>0){
						sortList(wordsList);
						String ss = joinList(wordsList);
						labelList.add(ss);
					}
					i++;
				}else if(i<len-1 && dependencyArray[i+1].contains("amod")){
					//4.处理advmod + amod
					Set<String> wordsSet = new HashSet<>();//words用于判断该词是否已经添加进去
					List<String[]>wordsList = new ArrayList<>();
					for(int j=0;j<=1;j++){
						List<String[]>tempList = getWordsUnit(dependencyArray[i+j]);
						if(tempList!=null){
							for(int k=0;k<2;k++){
								String item = tempList.get(k)[0];
								if(!wordsSet.contains(item)){
									wordsList.add(tempList.get(k));
									wordsSet.add(item);
								}
							}
							
						}
						
					}
					if(wordsList.size()>0){
						sortList(wordsList);
						String ss = joinList(wordsList);
						labelList.add(ss);
					}
					i++;
				}else{
					//5.处理advmod
					List<String[]>wordsList = new ArrayList<>();
					List<String[]>tempList = getWordsUnit(dependencyArray[i]);
					if(tempList!=null){
						for(int k=0;k<2;k++){
							wordsList.add(tempList.get(k));
						}
					}
					if(wordsList.size()>0){
						sortList(wordsList);
						String ss = joinList(wordsList);
						labelList.add(ss);
					}
				}
			}
		}
		
		return labelList;
	}
	
	/**
	 * 解析依存二元关系
	 * @param line
	 * @param pattern
	 * @return
	 */
	public static List<String[]> getWordsUnit(String line){
		Matcher matcher = pattern.matcher(line);
		if(matcher.find()){
			if(matcher.groupCount() == 4){
				String[] words1 = new String[2];
				String[] words2 = new String[2];
				List<String[]>list = new ArrayList<>();
				words1[0] = matcher.group(1);
				words1[1] = matcher.group(2);
				words2[0] = matcher.group(3);
				words2[1] = matcher.group(4);
				list.add(words1);
				list.add(words2);
				return list;
			}
		}
		return null;
	}
	
	/**
	 * 对list按照位置进行排序
	 * @param list
	 */
	public static void sortList(List<String[]>list){
		Collections.sort(list, new Comparator<String[]>() {

			@Override
			public int compare(String[] o1, String[] o2) {
				// TODO Auto-generated method stub
				return o1[1].compareTo(o2[1]);
			}
		});
	}
	
	/**
	 * 将list中的字符串拼接起来
	 * @param list
	 * @return
	 */
	public static String joinList(List<String[]>list){
		String line = "";
		for(String[]ss:list){
			line += ss[0] + "\t||\t";
		}
		return line;
	}
}
