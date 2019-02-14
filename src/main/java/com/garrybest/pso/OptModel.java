package com.garrybest.pso;

/**
 * This interface defines the optimization model.
 * Please implement this interface according to your own problem.
 *
 * @Author: Fang Rui
 * @Date: 2018/6/7
 * @Time: 22:30
 */
public interface OptModel {


    /**
     * Evaluate the objective function value based on the current location
     *
     * @param location the location of a particle
     * @return the objective function value
     */
    double evalObj(Location location);

    /**
     * Calculate the constraint function value based on the current location,
     * where the equality constraint is treated as an inequality constraint.
     * If your model has no constraint, there is no need to implement this method.
     *
     * @param location the location of a particle
     * @return the constraint function value
     */
    default double evalConstr(Location location) {
        return 0;
    }

    /**
     * Specify the lower boundary of the location, please give
     * the minimum of the location by returning an array.
     *
     * @return the lower boundary of the location
     */
    default double[] getMinLoc() {
        return new double[0];
    }

    /**
     * Specify the upper boundary of the location, please give
     * the maximum of the location by returning an array.
     *
     * @return the upper boundary of the location
     */
    default double[] getMaxLoc() {
        return new double[0];
    }

    /**
     * Specify the lower boundary of the velocity, please give
     * the minimum of the velocity by returning an array.
     *
     * @return the lower boundary of the velocity
     */
    default double[] getMinVel() {
        return new double[0];
    }

    /**
     * Specify the upper boundary of the velocity, please give
     * the maximum of the velocity by returning an array.
     *
     * @return the upper boundary of the velocity
     */
    default double[] getMaxVel() {
        return new double[0];
    }


    /**
     * @return the dimension of the state variable
     */
    int getDimentions();

    /**
     * @return maximum iteration times，the default value if {@code 1000}
     */
    default int getMaxIter() {
        return 1000;
    }

    /**
     * Get the tolerant minimum objective function value of the model.
     * If the global best fitness value is smaller than it, the computation is
     * completed. Also, you don't need to set this value. The default value is
     * -99999, so the computation will not end until the iteration times has
     * reached the maximum value.
     *
     * @return the tolerant minimum objective function value of the model
     */
    default double getTolFitness() {
        return -99999;
    }

}
