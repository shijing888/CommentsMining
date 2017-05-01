package com.comments.clustering;

import java.util.List;

import com.comments.bean.Text;


/**
 * Base class for clustering algorithms.
 *
 * @param <T> the type of points that can be clustered
 * @version $Id: Clusterer.java 1519184 2013-08-31 15:22:59Z tn $
 * @since 3.2
 */
public abstract class ClusteringAlgorithm {

    /**
     * Perform a cluster analysis on the given set of {@link Clusterable} instances.
     *
     * @param points the set of {@link Clusterable} instances
     * @return a {@link List} of clusters
     * @throws MathIllegalArgumentException if points are null or the number of
     *   data points is not compatible with this clusterer
     * @throws ConvergenceException if the algorithm has not yet converged after
     *   the maximum number of iterations has been exceeded
     */
    public abstract List<Cluster> cluster(List<Text> points);

    /**
     * Returns the {@link DistanceMeasure} instance used by this clusterer.
     *
     * @return the distance measure
     */

    /**
     * Calculates the distance between two {@link Clusterable} instances
     * with the configured {@link DistanceMeasure}.
     *
     * @param p1 the first clusterable
     * @param p2 the second clusterable
     * @return the distance between the two clusterables
     */

}
