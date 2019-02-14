package com.garrybest.pso;

import java.util.Random;
import java.util.Vector;

/**
 * Abstract super class of PSO.
 * If you want to customize your own PSO algorithm, please extends this class.
 *
 * @Author: Fang Rui
 * @Date: 2018/6/29
 * @Time: 9:36
 */
public abstract class AbstractPSO {
    protected Vector<Particle> swarm = new Vector<>();
    protected final int swarmSize;
    protected double[] pBest;
    protected Vector<Location> pBestLocation = new Vector<>();
    protected double gBest;
    protected Location gBestLocation;
    protected Random generator = new Random();
    protected boolean isWarmStart = false;
    protected double[] initVariableState;

    protected AbstractPSO(int swarmSize) {
        this.swarmSize = swarmSize;
        this.pBest = new double[swarmSize];
    }

    protected AbstractPSO(int swarmSize, double[] initVariableState) {
        this(swarmSize);
        this.isWarmStart = true;
        this.initVariableState = initVariableState;
    }

    protected abstract void initializeSwarm();

    protected abstract void execute();

    public double getgBest() {
        return gBest;
    }

    public Location getgBestLocation() {
        return gBestLocation;
    }

}
