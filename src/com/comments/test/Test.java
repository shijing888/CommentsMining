package com.comments.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Test {

	public static void main(String args[]){
		String[] lineArray = "老板	||	还	||	送	||	".split("\t\\|\\|\t");
		for(String i:lineArray)
			System.out.println(i);
		System.out.println(lineArray.length);
		double[] arr = {1,2,32,4,5,6};
		System.out.println(Arrays.asList(arr).toString());
	}
	
	public static void sortList(List<String[]>list){
		Collections.sort(list, new Comparator<String[]>() {

			@Override
			public int compare(String[] o1, String[] o2) {
				// TODO Auto-generated method stub
				return o1[1].compareTo(o2[1]);
			}
		});
	}
}
