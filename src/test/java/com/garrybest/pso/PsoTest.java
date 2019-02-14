package com.garrybest.pso;

import com.garrybest.pso.model.*;
import org.junit.Test;

/**
 * @Author: Fang Rui
 * @Date: 2018/6/20
 * @Time: 21:13
 */
public class PsoTest {

    @Test
    public void testSimplePso() {
        new HybridPSO(new SimpleModel_1()).execute();
        new HybridPSO(new SimpleModel_2(), 20).execute();
    }

    @Test
    public void testDifficultPso() {
        new HybridPSO(new DifficultModel_1()).execute();
        new HybridPSO(new DifficultModel_2()).execute();
        new HybridPSO(new DifficultModel_3()).execute();
    }

}
