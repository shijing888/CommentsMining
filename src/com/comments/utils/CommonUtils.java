package com.comments.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import edu.stanford.nlp.util.StringUtils;

import java.util.Set;

public class CommonUtils {

	public static void main(String args[]){
//		String path = "./dict/stopwords.txt";
//		Set<String> set = loadSet(path);
//		System.out.println(set.size());
		
		String vecPath = "./commentsData/trainData/vectors.txt";
		Map<String, Double[]>map = loadVecMap(vecPath,null);
		for(Entry<String, Double[]>entry:map.entrySet()){
			System.out.println(entry.getKey());
			System.out.println(StringUtils.join(Arrays.asList(entry.getValue())));
		}
	}
	
	/**
	 * 加载set集合
	 * @param path
	 * @return
	 */
	public static Set<String> loadSet(String path){
		Set<String> set = new HashSet<>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path)), "utf-8"));
			String line;
			while((line = reader.readLine()) != null){
				set.add(line);
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
		}
	
		return set;
	}
	
	/**
	 * 加载word2vec词典
	 * @param path
	 * @return
	 */
	public static Map<String, Double[]> loadVecMap(String path,int vecLens[]){
		Map<String, Double[]> map = new HashMap<>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path)), "utf-8"));
			String line;
			while((line = reader.readLine()) != null){
				String[] lineArray = line.split(" ");
				if(lineArray != null && lineArray.length > 2){
					Double[] array = new Double[lineArray.length - 1];
					for(int i=1;i<lineArray.length;i++){
						array[i-1] = Double.parseDouble(lineArray[i]);
					}
					map.put(lineArray[0], array);
					if(vecLens[0]==0){
						vecLens[0] = lineArray.length -1 ;
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
		}
		
		return map;
	}
	
	 /**
	    * 计算余弦相似性
	    * @param a
	    * @param b
	    * @param n
	    * @return
	    */
	   public static double calCosSim(double[]a,double[]b,int n){
		   double res = 0;
		   double a1 = 0,b1 = 0;
		   for(int i=0;i<n;i++){
			   a1 += a[i] * a[i];
			   b1 += b[i] * b[i];
			   res += a[i] * b[i];
		   }
		   if(a1>0){
			   a1 = Math.sqrt(a1);
		   }
		   if(b1>0){
			   b1 = Math.sqrt(b1);
		   }
		   if(a1 == 0 || b1 == 0 || res == 0)
			   return 0;
		   return res/(a1 * b1);
	   }

}
