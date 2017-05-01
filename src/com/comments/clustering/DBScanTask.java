package com.comments.clustering;

import java.util.List;

import com.comments.bean.Text;

/**
 * 聚类主程序入口
 *
 */
public class DBScanTask {
	  
	public static void main(String args[]){
		String rpath = "./commentsData/candidateLabels.txt";
		String vecPath = "./commentsData/trainData/vectors.txt";
		String sentimentPath = "./dict/sentimentWords.txt";
		String outputPath = "./commentsData/output/labelOutput.txt";
		double clusterRadius = 0.9;//dbscan聚类半径，也即相似性阈值
		int minPts = 10;//dbscan聚类最少邻居数
		List<Text> points = Text.loadTexts(rpath, vecPath,sentimentPath);
		//聚类开始
		DBScanTask.dbCluster(points,outputPath,clusterRadius,minPts);
	}
	
	/**
	 * dbscan聚类
	 * @param points，数据集
	 * @return，聚类结果
	 */
	public static List<Cluster> dbCluster(List<Text> points,String outputPath,double clusterRadius,int minPts){
		Dbscan dbscan = new Dbscan(clusterRadius, minPts);
		List<Cluster> clusters = dbscan.cluster(points);
		int vecLen = clusters.get(0).getInstances().get(0).getTextVect().length;
		if(clusters != null){
			//计算每个聚类中心，并将与聚类中心最近的点作为抽取的标签
			dbscan.getNearestLabel(clusters, vecLen);
			//将聚类结果写入文件
			dbscan.writeCluster(clusters);
			//将抽取的标签写入文件
			dbscan.writeLabels(clusters, outputPath);
		}
	
		return clusters;
	}
	
	
}
