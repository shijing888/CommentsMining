package com.comments.clustering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.comments.bean.Text;

/**
 * Cluster holding a set of {@link Clusterable} points.
 * @param <T> the type of points that can be clustered
 * @version $Id: Cluster.java 1461862 2013-03-27 21:48:10Z tn $
 * @since 3.2
 */
public class Cluster implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//评论列表
    private final List<Text> points;
    //中心点向量值
    private double[] centerVecs;
    //最近点下标
    private int nearestIndex;
    /**
     * Build a cluster centered at a specified point.
     */
    public Cluster() {
    	this.points = new ArrayList<Text>();
    }
   
    /**
     * Add a point to this cluster.
     * @param instance to add
     */
    public void addInstance(final Text instance) {
        points.add(instance);
    }

    /**
     * Get the points contained in the cluster.
     * @return points contained in the cluster
     */
    public List<Text> getInstances() {
        return points;
    }
   
    public double[] getCenterVecs() {
		return centerVecs;
	}

	public void setCenterVecs(double[] centerVecs) {
		this.centerVecs = centerVecs;
	}

	public int getNearestIndex() {
		return nearestIndex;
	}

	public void setNearestIndex(int nearestIndex) {
		this.nearestIndex = nearestIndex;
	}

	public static void main(String[] args) {
    	
    }
}
