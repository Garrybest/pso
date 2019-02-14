package com.garrybest.pso;

/**
 * @Author: Fang Rui
 * @Date: 2018/6/7
 * @Time: 17:43
 */
public class PSOUtil implements PSOConstants {

    /**
     * Find the minimum element in the array.
     *
     * @param list the array
     * @return the index of the minimum element
     */
    public static int getMinPos(double[] list) {
        int pos = 0;
        double minValue = list[0];
        for (int i = 0; i < list.length; i++) {
            if (list[i] < minValue) {
                pos = i;
                minValue = list[i];
            }
        }
        return pos;
    }

    /**
     * Find the minimum element in the array, only if it is {@code isFeasible}.
     *
     * @param list         the array
     * @param feasibleList feasible list of the array
     * @param isFeasible   whether the feasible or infeasible element should be found
     * @return the index of the minimum element
     */
    public static int getMinPos(double[] list, boolean[] feasibleList, boolean isFeasible) {
        int pos = -1;
        double minValue = 0;
        for (int i = 0; i < list.length; i++) {
            if (!feasibleList[i] && isFeasible)
                continue;
            if (pos == -1) {
                pos = i;
                minValue = list[i];
            } else {
                if (list[i] < minValue) {
                    pos = i;
                    minValue = list[i];
                }
            }
        }
        return pos;
    }

    /**
     * Determine if the particle is feasible
     */
    public static boolean isFeasible(double[] constrValueList) {
        boolean isFeasible = true;
        for (double aConstrValue : constrValueList) {
            if (aConstrValue > 0)
                isFeasible = false;
        }
        return isFeasible;
    }

    public static double restrictByBoundary(double val, double upper, double lower) {
        if (val < lower)
            return lower;
        else if (val > upper)
            return upper;
        else
            return val;
    }

    /**
     * Restrict the val to the middle point.
     */
    public static double restrictByBoundary(double val, double upper, double lower, double previous) {
        if (val < lower)
            return (lower + previous) / 2;
        else if (val > upper)
            return (upper + previous) / 2;
        else
            return val;
    }

    /**
     * Calculate the norm of a vector.
     */
    public static double getVecNorm(double[] vector) {
        double norm = 0;
        for (double aVector : vector) {
            norm += aVector * aVector;
        }
        norm = Math.sqrt(norm);
        return norm;
    }

    public static double randomBool(double random) {
        return random < 0.5 ? 0 : 1;
    }

    public static double sigmoid(double vel, double random) {
        double val = 1 / (1 + Math.exp(-vel));
        return val < random ? 0 : 1;
    }
}
