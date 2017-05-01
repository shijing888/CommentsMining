package com.comments.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.comments.bean.Text;
import com.comments.clustering.Dbscan.InstanceStatus;
import com.comments.utils.CommonUtils;

public class Neighbors {
   
   private static final double JACCARDRETAINTHRESHOLD = 0.9;
   
	/**
	 * 用于获得邻居点
	 * @param current，当前实例点
	 * @param points，数据集
	 * @return 邻居列表
	 */
   public List<Text> getNeighbors(Map<Text, InstanceStatus> visited,
		   Text current, List<Text> points){
	   	int n = current.getTextVect().length;
	   	List<Text> nbList = new ArrayList<Text>();
		for(Text point :points){
			if (visited.get(point) != null) {
               continue;
           }
			
			double res = CommonUtils.calCosSim(point.getTextVect(), current.getTextVect(), n);
			System.out.println(current.getText()+" 与 "+point.getText()+"的相似度为:"+res);
			if(res > JACCARDRETAINTHRESHOLD)
				nbList.add(point);
		}
		
		return nbList;
	}
	
}
