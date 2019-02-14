package com.garrybest.pso;

/**
 * @Author: Fang Rui
 * @Date: 2018/6/7
 * @Time: 17:43
 */

public class Velocity {
    // store the Velocity in an array to accommodate multi-dimensional problem space
    private double[] vel;

    public Velocity(double[] vel) {
        super();
        this.vel = vel;
    }

    public double[] getVel() {
        return vel;
    }

    public void setVel(double[] vel) {
        this.vel = vel;
    }

}
