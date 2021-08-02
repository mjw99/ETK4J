package com.wildbitsfoundry.etk4j.math.specialfunctions;

import com.wildbitsfoundry.etk4j.math.complex.Complex;
import static com.wildbitsfoundry.etk4j.math.specialfunctions.Bessel.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BesselTest {

    @Test
    public void testOfThirdKind() {
        double[] ka = new double[1];
        double[] ka1 = new double[1];
        nonexpbesska01(1, 0.5, ka, ka1);
        assertEquals(2.7310097082117863, ka[0], 1e-12);
        assertEquals(12.448148218621055, ka1[0], 1e-12);

        Complex[] kaComplex = new Complex[1];
        Complex[] kaComplex1 = new Complex[1];
        besska01(0.1, new Complex(0.25, 0.25), kaComplex, kaComplex1);
        assertTrue(new Complex(1.1849119210357626, -0.7275785808287022).isClose(kaComplex[0], 1e-12));
        assertTrue(new Complex(5.9761290606773427E-5, -8.314722061605179E-5).isClose(kaComplex1[0], 1e-12));

        nonexpbesska01(0.1, new Complex(0.25, 0.25), kaComplex, kaComplex1);
        assertTrue(new Complex(1.7052906588675252, -0.5287719697447256).isClose(kaComplex[0], 1e-12));
        assertTrue(new Complex(1.00763134929447E-4, -8.445959019188772E-5).isClose(kaComplex1[0], 1e-12));

        nonexpbesska01(0.0, new Complex(5, 5), kaComplex, kaComplex1);
        assertTrue(new Complex(0.4320756434783003, -0.17324024390967138).isClose(kaComplex[0], 1e-12));
        assertTrue(new Complex(0.4453282558120745, -0.20249714367635196).isClose(kaComplex1[0], 1e-12));

        nonexpbesska01(1.0, new Complex(5, 5), kaComplex, kaComplex1);
        assertTrue(new Complex(0.44532825581206464, -0.2024971436936182).isClose(kaComplex[0], 1e-12));
        assertTrue(new Complex(0.4806418659019877, -0.3028053238162896).isClose(kaComplex1[0], 1e-12));

        nonexpbesska01(0.0, new Complex(0.5, 0.5), kaComplex, kaComplex1);
        assertTrue(new Complex(1.2740700057330194, -0.43052443373915755).isClose(kaComplex[0], 1e-12));
        assertTrue(new Complex(1.6928912856511094, -1.1095435340610968).isClose(kaComplex1[0], 1e-12));

        nonexpbesska01(0.0, new Complex(1.0, Math.sqrt(1.25)), kaComplex, kaComplex1);
        assertTrue(new Complex(0.9027895696248687, -0.35734124114045224).isClose(kaComplex[0], 1e-12));
        assertTrue(new Complex(1.0230661523935811, -0.6250311866955051).isClose(kaComplex1[0], 1e-12));

        nonexpbesska01(0.0, new Complex(1.0, Math.sqrt(1.25) + 0.5), kaComplex, kaComplex1);
        assertTrue(new Complex(0.7827492386732262, -0.3930346699919637).isClose(kaComplex[0], 1e-12));
        assertTrue(new Complex(0.8169685720097325, -0.6043692159144972).isClose(kaComplex1[0], 1e-12));

        nonexpbesska01(0.0, new Complex(1.0, Math.sqrt(1.25) - 0.5), kaComplex, kaComplex1);
        assertTrue(new Complex(1.0424992536107143, -0.2574424664855407).isClose(kaComplex[0], 1e-12));
        assertTrue(new Complex(1.338125723984699, -0.5292382369845088).isClose(kaComplex1[0], 1e-12));
    }
}
