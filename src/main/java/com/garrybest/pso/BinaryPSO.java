package com.garrybest.pso;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The core algorithm of Binary PSO.
 *
 * @Author: Fang Rui
 * @Date: 2018/6/29
 * @Time: 17:05
 */
public class BinaryPSO extends AbstractPSO implements PSOConstants {

    private static Logger logger = LogManager.getLogger(BinaryPSO.class);

    private OptModel optModel;
    private double[] fitness;
    private boolean isGBestfeasible = false;

    public BinaryPSO(OptModel optModel) {
        this(optModel, (int) (10 + 2 * Math.sqrt(optModel.getDimentions())));
    }

    public BinaryPSO(OptModel optModel, int swarmSize) {
        super(swarmSize);
        this.optModel = optModel;
        this.fitness = new double[swarmSize];
    }

    public BinaryPSO(OptModel optModel, double[] initVariableState) {
        this(optModel, (int) (10 + 2 * Math.sqrt(optModel.getDimentions())), initVariableState);
    }

    public BinaryPSO(OptModel optModel, int swarmSize, double[] initVariableState) {
        super(swarmSize, initVariableState);
        this.optModel = optModel;
        this.fitness = new double[swarmSize];
    }

    @Override
    protected void initializeSwarm() {
        Particle p;
        int n = optModel.getDimentions();

        for (int i = 0; i < swarmSize; i++) {
            p = new Particle();

            double[] loc = new double[n];
            double[] vel = new double[n];

            if (isWarmStart) {
                for (int j = 0; j < n; j++) {
                    loc[j] = initVariableState[j];
                }
            } else {
                for (int j = 0; j < n; j++) {
                    loc[j] = PSOUtil.randomBool(generator.nextDouble());
                }
            }
            Location location = new Location(loc);
            Velocity velocity = new Velocity(vel);

            p.setLocation(location);
            p.setVelocity(velocity);
            swarm.add(p);

            double violation = optModel.evalConstr(location);
            if (violation > 0) {
                fitness[i] = violation + PUNISHMENT;
            } else {
                fitness[i] = optModel.evalObj(location);
                isGBestfeasible = true;
            }

            pBest[i] = fitness[i];
            pBestLocation.add(location);
        }

        int bestParticleIndex = PSOUtil.getMinPos(fitness);
        gBest = fitness[bestParticleIndex];
        gBestLocation = swarm.get(bestParticleIndex).getLocation();
    }

    @Override
    public void execute() {
        initializeSwarm();

        int iterNum = 0;
        int n = optModel.getDimentions();
        int maxIter = optModel.getMaxIter();


        double tol = 1e6;
        double w;

        while (iterNum < maxIter && tol > 0) {
            w = W_UPPERBOUND - (((double) iterNum) / maxIter) * (W_UPPERBOUND - W_LOWERBOUND);

            for (int i = 0; i < swarmSize; i++) {
                double r1 = generator.nextDouble();
                double r2 = generator.nextDouble();

                Particle p = swarm.get(i);

                double[] loc = new double[n];
                double[] vel = new double[n];
                for (int j = 0; j < n; j++) {
                    vel[j] = (w * p.getVelocity().getVel()[j]) +
                            (r1 * C1) * (pBestLocation.get(i).getLoc()[j] - p.getLocation().getLoc()[j]) +
                            (r2 * C2) * (gBestLocation.getLoc()[j] - p.getLocation().getLoc()[j]);
                    loc[j] = PSOUtil.sigmoid(vel[j], generator.nextDouble());
                }
                p.setLocation(new Location(loc));
                p.setVelocity(new Velocity(vel));

            }

            Set<Particle> pool = new HashSet<>();
            int hybridPoolSize = (int) (swarmSize * HYBRID_PROBABILITY);
            if ((hybridPoolSize & 1) == 1) // 如果是奇数
                hybridPoolSize++;
            while (pool.size() < hybridPoolSize) {
                Particle p = swarm.get((int) (Math.random() * swarmSize));
                pool.add(p);
            }
            Iterator iter = pool.iterator();
            while (iter.hasNext()) {
                Particle p1 = (Particle) iter.next();
                Particle p2 = (Particle) iter.next();

                double velNorm1 = PSOUtil.getVecNorm(p1.getVelocity().getVel()); // |v1|
                double velNorm2 = PSOUtil.getVecNorm(p2.getVelocity().getVel()); // |v2|
                double[] tempVel = new double[n];
                for (int i = 0; i < p1.getVelocity().getVel().length; i++) {
                    tempVel[i] = p1.getVelocity().getVel()[i] + p2.getVelocity().getVel()[i];
                }
                double tempNorm = PSOUtil.getVecNorm(tempVel); // |v1 + v2|

                double coefficient1 = velNorm1 / tempNorm; // |v1| / |v1 + v2|
                double coefficient2 = velNorm2 / tempNorm; // |v2| / |v1 + v2|

                double[] loc1 = new double[n];
                double[] loc2 = new double[n];
                double[] vel1 = new double[n];
                double[] vel2 = new double[n];
                for (int i = 0; i < n; i++) {
                    vel1[i] = tempVel[i] * coefficient1;
                    vel2[i] = tempVel[i] * coefficient2;
                    loc1[i] = PSOUtil.sigmoid(vel1[i], generator.nextDouble());
                    loc2[i] = PSOUtil.sigmoid(vel2[i], generator.nextDouble());
                }
                p1.setLocation(new Location(loc1));
                p1.setVelocity(new Velocity(vel1));
                p2.setLocation(new Location(loc2));
                p2.setVelocity(new Velocity(vel2));
            }

            for (int i = 0; i < swarmSize; i++) {
                Location location = swarm.get(i).getLocation();

                double violation = optModel.evalConstr(location);
                if (violation > 0) {
                    fitness[i] = violation + PUNISHMENT;
                } else {
                    fitness[i] = optModel.evalObj(location);
                    isGBestfeasible = true;
                }

                if (fitness[i] < pBest[i]) {
                    pBest[i] = fitness[i];
                    pBestLocation.set(i, location);
                }
            }

            int bestParticleIndex = PSOUtil.getMinPos(fitness);
            if (fitness[bestParticleIndex] < gBest) {
                gBest = fitness[bestParticleIndex];
                gBestLocation = swarm.get(bestParticleIndex).getLocation();
            }

            if (isGBestfeasible)
                tol = gBest - optModel.getTolFitness();
            logger.info("ITERATION " + iterNum + ": Value: " + gBest + "  " + isGBestfeasible);
            iterNum++;
        }

        if (isGBestfeasible) {
            logger.info("Solution found at iteration " + iterNum + ", best fitness value: " + gBest);
        } else {
            logger.warn("Solution not found");
        }
    }

    public boolean isGBestfeasible() {
        return isGBestfeasible;
    }
}
