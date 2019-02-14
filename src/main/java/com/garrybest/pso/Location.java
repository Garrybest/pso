package com.garrybest.pso;

/**
 * Location of a particle
 *
 * @Author: Fang Rui
 * @Date: 2018/6/7
 * @Time: 17:43
 */

public class Location {
    // store the Location in an array to accommodate multi-dimensional problem space
    private double[] loc;
    private double[] constrViolation;

    public Location(double[] loc) {
        super();
        this.loc = loc;
    }

    public double[] getLoc() {
        return loc;
    }

    public void setLoc(double[] loc) {
        this.loc = loc;
    }

    public double[] getConstrViolation() {
        return constrViolation;
    }

    public void setConstrViolation(double[] constrViolation) {
        this.constrViolation = constrViolation;
    }
}
