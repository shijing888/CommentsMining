package com.comments.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


import com.comments.bean.Text;
import com.comments.clustering.Dbscan.InstanceStatus;

public class ClusterStructure {

   /**
    * @param points 数据
    * @return 聚类结果
    */
	public List<Cluster> clusterAlgorithm(final List<Text> points){
		Neighbors nearNeighbors = new Neighbors();
		List<Cluster> clusters = new ArrayList<Cluster>();
		//数据访问标记
        final Map<Text, InstanceStatus> visited = new HashMap<Text, InstanceStatus>();
        //初始聚类标号
        int clusterLabel = -1; 
        
        for (final Text instance : points) {
        	//从未被标记的点中找其邻居
            if (visited.get(instance) != null) {
                continue;
            }
            
            final List<Text> neighbors = nearNeighbors.getNeighbors(visited, instance, points);
            neighbors.remove(instance);
            if (neighbors.size() >= Dbscan.getMinPts()) {
                Cluster cluster = new Cluster();
                clusterLabel++;
                cluster = expandCluster(nearNeighbors,cluster, 
                		instance, neighbors, points, visited, clusterLabel);
                if(cluster.getInstances().size() >= Dbscan.getMinPts()){
                	clusters.add(cluster);
                }else{
                	clusterLabel--;
                }
            } else {
                visited.put(instance, InstanceStatus.NOISE);
            }
           
        }
        
        //对聚类结果按照数量排序
        clusters = sortByClusterSize(clusters);
        return clusters;
	}
	
	 /**
     * 扩展簇，将密度可达点加入簇中
     *
     * @param cluster 簇
     * @param instance 实例点
     * @param neighbors 邻居列表
     * @param points 数据集
     * @param visited 用于记录访问点
     * @return 扩展后的簇
     */
    private Cluster expandCluster(Neighbors nearNeighbors,
    							  final Cluster cluster,
                                  final Text instance,
                                  final List<Text> neighbors,
                                  final List<Text> points,
                                  final Map<Text, InstanceStatus> visited,
                                  int clusterLabel) {
        instance.setCluster(clusterLabel);
    	cluster.addInstance(instance);
        visited.put(instance, InstanceStatus.PART_OF_CLUSTER);

        Queue<Text> seeds = new LinkedList<Text>(neighbors);
        while (!seeds.isEmpty()) {
        	final Text current = seeds.poll();
        	InstanceStatus pStatus = visited.get(current);
        	// only check non-visited points
            if (pStatus == null) {
            	final List<Text> currentNeighbors = nearNeighbors.getNeighbors(visited, current, points);
            	if (currentNeighbors.size() >= Dbscan.getMinPts()) {
            		seeds.add(current);
            	}
            }

            if (pStatus != InstanceStatus.PART_OF_CLUSTER) {
                visited.put(current, InstanceStatus.PART_OF_CLUSTER);
                current.setCluster(clusterLabel);
                cluster.addInstance(current);
            }
        }

        return cluster;
    }
    
    /**
     * 对聚类结果按数量进行降序排列
     * @param clusters
     * @return
     */
	private List<Cluster> sortByClusterSize(List<Cluster> clusters){
    	Collections.sort(clusters, new Comparator<Cluster>(
    			) {
					public int compare(Cluster o1, Cluster o2) {
						// TODO Auto-generated method stub
						return o2.getInstances().size() - o1.getInstances().size();
					}
		});
    	return clusters;
    }
    
}
