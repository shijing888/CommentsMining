package com.comments.clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.comments.bean.Text;
import com.comments.utils.CommonUtils;

public class Dbscan extends ClusteringAlgorithm{

	
    /** Maximum radius of the neighborhood to be considered. */
    private static double eps;

    /** Minimum number of points needed for a cluster. */
    private static int minPts;
    
    /** Status of a point during the clustering process. */
    public enum InstanceStatus {
        /** The point has is considered to be noise. */
        NOISE,
        /** The point is already part of a cluster. */
        PART_OF_CLUSTER
    }

    /**
     * Creates a new instance of a DBSCANClusterer.
     *
     * @param eps maximum radius of the neighborhood to be considered
     * @param minPts minimum number of points needed for a cluster
     * @param measure the distance measure to use
     * @throws NotPositiveException if {@code eps < 0.0} or {@code minPts < 0}
     */
	public Dbscan(final double eps, final int minPts) {
		if (eps < 0.0d) {
			throw new IllegalArgumentException(String.valueOf(eps));
		}
		if (minPts < 0) {
			throw new IllegalArgumentException(String.valueOf(minPts));
		}
		Dbscan.eps = eps;
		Dbscan.minPts = minPts;
	}

    /**
     * Returns the maximum radius of the neighborhood to be considered.
     * @return maximum radius of the neighborhood
     */
    public static double getEps() {
        return eps;
    }

    /**
     * Returns the minimum number of points needed for a cluster.
     * @return minimum number of points needed for a cluster
     */
    public static int getMinPts() {
        return minPts;
    }

	/**
     * Performs DBSCAN cluster analysis.
     *
     * @param points the points to cluster
     * @return the list of clusters
     * @throws NullArgumentException if the data points are null
     */
    @Override
    public List<Cluster> cluster(final List<Text> points) {
        if (points == null || points.isEmpty())
        	return null;
        
        ClusterStructure clusterStructure = new ClusterStructure();
        return clusterStructure.clusterAlgorithm(points);
    }
    
    /**
     * 将聚类结果写入文件
     * @param path,保存路径
     * @param clusters，聚类结果集
     */
    public void writeCluster(List<Cluster> clusters){
    	int i = 1;
    	for(Cluster cluster:clusters){
    		String path = "./commentsData/output/" + i++;
    		try {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(new File(path)), "utf-8"));
				List<Text> texts = cluster.getInstances();
				StringBuilder builder = new StringBuilder();
				for(Text text:texts){
					builder.delete(0, builder.length());
					builder.append(text.getText()).append("\n");
					writer.write(builder.toString());
				}
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
	}
    

    /**
     * 将抽取的标签写入文件
     * @param path,保存路径
     * @param clusters，聚类结果集
     */
    public void writeLabels(List<Cluster> clusters,String path){
    	BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File(path)), "utf-8"));
			for(Cluster cluster:clusters){
				writer.write(cluster.getInstances().get(cluster.getNearestIndex()).getText()+"\n");
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
    
    /**
     * 获取距离中心点最近的点
     * @param clusters
     * @param vecLen
     */
    public void getNearestLabel(List<Cluster> clusters,int vecLen){
    	calCenterVecs(clusters, vecLen);
    	for(Cluster cluster:clusters){
    		List<Text> texts = cluster.getInstances();
    		double min=Double.MAX_VALUE;
    		int minIndex=0;
    		for(int i=0;i<texts.size();i++){
    			double res = CommonUtils.calCosSim(texts.get(i).getTextVect(), cluster.getCenterVecs(), vecLen);
				if(res < min){
					min = res;
					minIndex = i;
				}
    		}
    		cluster.setNearestIndex(minIndex);
    	}
    	
    }
    
    
    /**
     * 计算每个聚类的中心向量 
     * @param clusters
     * @param vecLen
     */
    public void calCenterVecs(List<Cluster> clusters,int vecLen){
    	for(Cluster cluster:clusters){
    		int counts = cluster.getInstances().size();
    		double[] vec = new double[vecLen];
    		for(int i=0;i<counts;i++){
    			Text text = cluster.getInstances().get(i);
    			for(int j=0;j<vecLen;j++){
    				vec[j] += text.getTextVect()[j];
    			}
    		}
    		for(int j=0;j<vecLen;j++){
    			vec[j] /= counts;
    		}
    		cluster.setCenterVecs(vec);
    	}
    }
   
}
