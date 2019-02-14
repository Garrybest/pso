package com.garrybest.pso;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Hybrid PSO algorithm with gaussian mutation.
 *
 * @Author: Fang Rui
 * @Date: 2018/6/29
 * @Time: 17:05
 */
public class HybridPSO extends AbstractPSO implements PSOConstants {

    private static Logger logger = LogManager.getLogger(HybridPSO.class);

    private OptModel optModel;
    private double[] fitness;
    private boolean isGBestfeasible = false;

    /**
     * Constructs a hybrid PSO solver, the default size of swarm is {@code 10 + 2 * sqrt(n)}
     *
     * @param optModel the model of the problem you want to solve
     */
    public HybridPSO(OptModel optModel) {
        this(optModel, (int) (10 + 2 * Math.sqrt(optModel.getDimentions())));
    }


    /**
     * Constructs a hybrid PSO solver with a specified swarm size
     *
     * @param optModel  the model of the problem you want to solve
     * @param swarmSize the size of the PSO swarm
     */
    public HybridPSO(OptModel optModel, int swarmSize) {
        super(swarmSize);
        this.optModel = optModel;
        this.fitness = new double[swarmSize];
    }

    /**
     * Constructs a hybrid PSO solver, the default size of swarm is {@code 10 + 2 * sqrt(n)}.
     * The solver uses a swarm start.
     *
     * @param optModel          the model of the problem you want to solve
     * @param initVariableState initial variable state
     */
    public HybridPSO(OptModel optModel, double[] initVariableState) {
        this(optModel, (int) (10 + 2 * Math.sqrt(optModel.getDimentions())), initVariableState);
    }

    /**
     * Constructs a hybrid PSO solver with a specified swarm size.
     * The solver uses a swarm start.
     *
     * @param optModel          the model of the problem you want to solve
     * @param swarmSize         the size of the PSO swarm
     * @param initVariableState initial variable state
     */
    public HybridPSO(OptModel optModel, int swarmSize, double[] initVariableState) {
        super(swarmSize, initVariableState);
        this.optModel = optModel;
        this.fitness = new double[swarmSize];
    }

    @Override
    protected void initializeSwarm() {
        Particle p;
        int n = optModel.getDimentions();
        double[] minLoc = optModel.getMinLoc();
        double[] maxLoc = optModel.getMaxLoc();
        double[] minVel = optModel.getMinVel();
        double[] maxVel = optModel.getMaxVel();

        for (int i = 0; i < swarmSize; i++) {
            p = new Particle();

            double[] loc = new double[n];
            double[] vel = new double[n];

            // Randomize the location and velocity of particles
            if (isWarmStart) {
                for (int j = 0; j < n; j++) {
                    loc[j] = initVariableState[j];
                    vel[j] = minVel[j] + generator.nextDouble() * (maxVel[j] - minVel[j]);
                }
            } else {
                for (int j = 0; j < n; j++) {
                    loc[j] = minLoc[j] + generator.nextDouble() * (maxLoc[j] - minLoc[j]);
                    vel[j] = minVel[j] + generator.nextDouble() * (maxVel[j] - minVel[j]);
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

        // find gBest
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
        double[] minLoc = optModel.getMinLoc();
        double[] maxLoc = optModel.getMaxLoc();
        double[] minVel = optModel.getMinVel();
        double[] maxVel = optModel.getMaxVel();


        double tol = 1e6;
        double w; // inertia weight

        while (iterNum < maxIter && tol > 0) {
            w = W_UPPERBOUND - (((double) iterNum) / maxIter) * (W_UPPERBOUND - W_LOWERBOUND); // decrease gradually

            for (int i = 0; i < swarmSize; i++) {
                double r1 = generator.nextDouble();
                double r2 = generator.nextDouble();

                Particle p = swarm.get(i);

                // Step 1：update velocity
                double[] newVel = new double[n];
                for (int j = 0; j < n; j++) {
                    double tempVel = (w * p.getVelocity().getVel()[j]) +
                            (r1 * C1) * (pBestLocation.get(i).getLoc()[j] - p.getLocation().getLoc()[j]) +
                            (r2 * C2) * (gBestLocation.getLoc()[j] - p.getLocation().getLoc()[j]);
                    newVel[j] = PSOUtil.restrictByBoundary(tempVel, maxVel[j], minVel[j]);
                }
                p.setVelocity(new Velocity(newVel));

                // Step 2：update location
                double[] newLoc = new double[n];
                for (int j = 0; j < n; j++) {
                    double previousLoc = p.getLocation().getLoc()[j];
                    double tempLoc = previousLoc + newVel[j];
                    newLoc[j] = PSOUtil.restrictByBoundary(tempLoc, maxLoc[j], minLoc[j], previousLoc);
                }
                p.setLocation(new Location(newLoc));
            }

            // Step 3：hybridization
            Set<Particle> pool = new HashSet<>();
            int hybridPoolSize = (int) (swarmSize * HYBRID_PROBABILITY);
                if ((hybridPoolSize & 1) == 1) // odd number
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
                    double pb = Math.random();
                    loc1[i] = pb * p1.getLocation().getLoc()[i] + (1 - pb) * p2.getLocation().getLoc()[i];
                    loc2[i] = pb * p2.getLocation().getLoc()[i] + (1 - pb) * p1.getLocation().getLoc()[i];
                    vel1[i] = PSOUtil.restrictByBoundary(tempVel[i] * coefficient1, maxVel[i], minVel[i]);
                    vel2[i] = PSOUtil.restrictByBoundary(tempVel[i] * coefficient2, maxVel[i], minVel[i]);
                }
                p1.setLocation(new Location(loc1));
                p1.setVelocity(new Velocity(vel1));
                p2.setLocation(new Location(loc2));
                p2.setVelocity(new Velocity(vel2));
            }

            // Step 4：gaussian mutation
            pool.clear();
            double mutationProbability = MUTATION_UPPERBOUND - (((double) iterNum) / maxIter) * (MUTATION_UPPERBOUND - MUTATION_LOWERBOUND);
            int mutationPoolSize = (int) (swarmSize * mutationProbability);
            while (pool.size() < mutationPoolSize) {
                Particle p = swarm.get((int) (Math.random() * swarmSize));
                pool.add(p);
            }
            iter = pool.iterator();
            // Get Gaussian mutation operator
            double[] mutationCoeff = new double[n];
            for (int i = 0; i < mutationCoeff.length; i++) {
                double sigma = (maxLoc[i] - minLoc[i]) * 0.1;
                mutationCoeff[i] = generator.nextGaussian() * sigma;
            }
            while (iter.hasNext()) {
                Particle p = (Particle) iter.next();
                double[] mutationLoc = new double[n];
                for (int i = 0; i < mutationLoc.length; i++) {
                    double previousLoc = p.getLocation().getLoc()[i];
                    double tempLoc = previousLoc + mutationCoeff[i];
                    mutationLoc[i] = PSOUtil.restrictByBoundary(tempLoc, maxLoc[i], minLoc[i], previousLoc);
                    mutationLoc[i] = tempLoc;
                }
                p.setLocation(new Location(mutationLoc));
            }

            for (int i = 0; i < swarmSize; i++) {
                Location location = swarm.get(i).getLocation();

                // Step 5：upate fitness value
                double violation = optModel.evalConstr(location);
                if (violation > 0) {
                    fitness[i] = violation + PUNISHMENT;
                } else {
                    fitness[i] = optModel.evalObj(location);
                    isGBestfeasible = true;
                }

                // Step 6：upate pBest
                if (fitness[i] < pBest[i]) {
                    pBest[i] = fitness[i];
                    pBestLocation.set(i, location);
                }
            }

            // Step 7：upate gBest
            int bestParticleIndex = PSOUtil.getMinPos(fitness);
            if (fitness[bestParticleIndex] < gBest) {
                gBest = fitness[bestParticleIndex];
                gBestLocation = swarm.get(bestParticleIndex).getLocation();
            }

            if (isGBestfeasible)
                tol = gBest - optModel.getTolFitness();
            logger.debug("ITERATION " + iterNum + ": Value: " + gBest + "  " + isGBestfeasible);
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
