package com.garrybest.pso.model;

import com.garrybest.pso.*;

/**
 * @Author: Fang Rui
 * @Date: 2018/6/19
 * @Time: 14:53
 */
public class SimpleModel_2 implements OptModel {

    @Override
    public double evalObj(Location location) {
        double[] x = location.getLoc();
        return -x[0] - 2 * x[1] + 0.5 * x[0] * x[0] + 0.5 * x[1] * x[1];
    }

    @Override
    public double evalConstr(Location location) {
        double[] x = location.getLoc();
        double constr = 0;

        if (2 * x[0] + 3 * x[1] - 6 > 0)
            constr += 2 * x[0] + 3 * x[1] - 6;
        if (x[0] + 4 * x[1] - 5 > 0)
            constr += x[0] + 4 * x[1] - 5;
        return constr;
    }

    @Override
    public int getDimentions() {
        return 2;
    }

    @Override
    public double[] getMinLoc() {
        return new double[]{0, 0};
    }

    @Override
    public double[] getMaxLoc() {
        return new double[]{10, 10};
    }

    @Override
    public double[] getMinVel() {
        return new double[]{-1, -1};
    }

    @Override
    public double[] getMaxVel() {
        return new double[]{1, 1};
    }
}
