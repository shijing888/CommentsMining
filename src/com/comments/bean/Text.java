package com.comments.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.comments.utils.CommonUtils;

import edu.stanford.nlp.util.StringUtils;

public class Text {

	/*评论字符串*/
	private String text;
	/*评论字符串的向量化*/
	private double[] textVect;
	/*类标号*/
	private int cluster;
	
	public void setCluster(int clusterIndex){
		this.cluster=clusterIndex;
	}
	public int getCluster(){
		return this.cluster;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public double[] getTextVect() {
		return textVect;
	}
	public void setTextVect(double[] textVect) {
		this.textVect = textVect;
	}
	
	/**
	 * 加载处理后的依存短句
	 * @param rpath
	 * @param vecPath
	 * @return
	 */
	public static List<Text> loadTexts(String rpath,String vecPath,String sentimentPath){
		List<Text> texts = new ArrayList<>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(rpath)), "utf-8"));
			String line;
			int vecLens[] = new int[1];
			Map<String, Double[]> vecMap = CommonUtils.loadVecMap(vecPath,vecLens);
			Set<String> sentimentDict = CommonUtils.loadSet(sentimentPath);
			int veclen = vecLens[0];
			while((line = reader.readLine()) != null){
				String[] lineArray = line.split("\t\\|\\|\t");
				if(lineArray == null || lineArray.length == 0)
					continue;
				line = StringUtils.join(Arrays.asList(lineArray),"");
				boolean isContainSentimentWords = false;
				Text text = new Text();
				double[] vecs = new double[veclen];
				int count = 0;
				for(int i=0;i<lineArray.length;i++){
					if(vecMap.containsKey(lineArray[i])){
						count++;
						Double[] vecArray = vecMap.get(lineArray[i]);
						for(int j=0;j<veclen;j++){
							vecs[j] += vecArray[j];
						}
					}
					if(sentimentDict.contains(lineArray[i]))
						isContainSentimentWords = true;
				}
				
				if(isContainSentimentWords){
					if(count>0){
						for(int j=0;j<veclen;j++){
							vecs[j] /= count;
						}
					}
					text.setText(line);
					text.setTextVect(vecs);
					texts.add(text);
				}
				
				
			}
			
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
		
		return texts;
	}
	
	@Test
	public void test(){
		String rpath = "./commentsData/candidateLabels.txt";
		String vecPath = "./commentsData/trainData/vectors.txt";
		String sentimentPath = "./dict/sentimentWords.txt";
		loadTexts(rpath, vecPath,sentimentPath);
	}
}
