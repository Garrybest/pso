package com.garrybest.pso;

/**
 * Constants of the solver.
 *
 * @Author: Fang Rui
 * @Date: 2018/6/7
 * @Time: 17:43
 */

public interface PSOConstants {
    double C1 = 1.4961;
    double C2 = 1.4961;
    double W_UPPERBOUND = 0.9;
    double W_LOWERBOUND = 0.1;
    double PUNISHMENT = 1e6;
    double HYBRID_PROBABILITY = 0.5;
    double MUTATION_UPPERBOUND = 0.3;
    double MUTATION_LOWERBOUND = 0.1;
}
