package com.wildbitsfoundry.etk4j.control;

import com.wildbitsfoundry.etk4j.math.complex.Complex;
import com.wildbitsfoundry.etk4j.util.NumArrays;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TransferFunctionTest {

    @Test
    public void testConstructors() {
        TransferFunction tf = new TransferFunction(new double[] { 10.0 }, new double[] { 1.0, 1.0 });
        assertEquals(10.0, tf.getMagnitudeAt(0.0), 1e-12);

    }

    @Test
    public void testMargins() {
        double[] numerator = { 1.0 };
        double[] denominator = { 1.0, 2.0, 1.0, 0.0 };

        TransferFunction tf = new TransferFunction(numerator, denominator);
        Margins margins = tf.getMargins();

        assertEquals(0.6823278038280193, margins.getGainCrossOverFrequency(), 1e-12);
        assertEquals(21.386389751875015, margins.getPhaseMargin(), 1e-12);
        assertEquals(0.9999999999999998, margins.getPhaseCrossOverFrequency(), 1e-12);
        assertEquals(6.02059991327962, margins.getGainMargin(), 1e-12);
        assertEquals(
                "Margins [GainMargin=6.02059991327962, PhaseMargin=21.386389751875015,"
                        + " GainCrossOverFrequency=0.6823278038280193, PhaseCrossOverFrequency=0.9999999999999998]",
                margins.toString());
    }

    @Test
    public void testPolesAndZeros() {
        double[] numerator = { 1.0 };
        double[] denominator = { 1.0, 2.0, 1.0, 0.0 };

        TransferFunction tf = new TransferFunction(numerator, denominator);

        Complex[] zeros = tf.getZeros();
        Complex[] poles = tf.getPoles();

        assertArrayEquals(new Complex[] {}, zeros);
        assertArrayEquals(new Complex[] { Complex.fromReal(-1.0000000209081399), Complex.fromReal(-0.9999999790918601),
                Complex.fromReal(-4.1910912494273124E-17) }, poles);

        zeros = new Complex[] { Complex.fromReal(-1.0) };
        poles = new Complex[] { Complex.fromReal(-5.0), Complex.fromReal(-1.0), Complex.fromReal(-2.0) };

        tf = new TransferFunction(zeros, poles);

        numerator = tf.getNumerator().getCoefficients();
        denominator = tf.getDenominator().getCoefficients();

        assertArrayEquals(new double[] { 10.0, 10.0 }, numerator, 1e-12);
        assertArrayEquals(new double[] { 1.0, 8.0, 17.0, 10.0 }, denominator, 1e-12);

        ZeroPoleGain zpk = new ZeroPoleGain(zeros, poles, 2.0);
        tf = new TransferFunction(zpk);
        tf.normalize();

        assertArrayEquals(zpk.getZeros(), tf.toZeroPoleGain().getZeros());
        assertArrayEquals(zpk.getPoles(), tf.toZeroPoleGain().getPoles());
        assertEquals(zpk.getGain(), tf.toZeroPoleGain().getGain(), 1e-12);
    }

    @Test
    public void testPolesZPKToTf() {
        double[] numerator = { 1.0, 2.0 };
        double[] denominator = { 4.0, 2.0, 1.0, 0.0 };

        TransferFunction tf = new TransferFunction(numerator, denominator);

        Complex[] zeros = tf.getZeros();
        Complex[] poles = tf.getPoles();

        ZeroPoleGain zpk = new ZeroPoleGain(zeros, poles, 2.0);
        tf = new TransferFunction(zpk);

        assertArrayEquals(zpk.getZeros(), tf.toZeroPoleGain().getZeros());
        assertArrayEquals(zpk.getPoles(), tf.toZeroPoleGain().getPoles());
        assertEquals(zpk.getGain(), tf.toZeroPoleGain().getGain(), 1e-12);
    }

    @Test
    public void testGettersAndEvaluation() {
        Complex[] poles = new Complex[] { Complex.fromReal(-1.0), Complex.fromReal(-1.0), Complex.fromReal(-1.0) };

        TransferFunction tf = new TransferFunction(10.0, poles);

        double phase = tf.getPhaseInDegreesAt(100.0);
        assertEquals(-268.2811839069496, phase, 1e-12);

        double[] frequencies = NumArrays.logSpace(-3, 3, 10);

        double[] magnitudeResponse = { 9.999985000018752, 9.999676843499255, 9.993041654128266, 9.851853368415734,
                7.462732134984385, 0.7462732134984399, 0.009851853368415734, 9.993041654128302E-5, 9.999676843499274E-7,
                9.999985000018753E-9 };

        double[] phaseResponse = { -0.17188728124350178, -0.7978246216992994, -3.7026276509801344, -17.13177941249893,
                -74.69637093434646, -195.30362906565347, -252.86822058750107, -266.29737234901984, -269.2021753783007,
                -269.8281127187565 };

        assertArrayEquals(magnitudeResponse, tf.getMagnitudeAt(frequencies), 1e-12);
        double[] systemPhaseResponse = tf.getPhaseInDegreesAt(frequencies);
        TransferFunction.unwrapPhase(systemPhaseResponse);
        assertArrayEquals(phaseResponse, systemPhaseResponse, 1e-12);
    }

    @Test
    public void testStepResponse() {
        // Test letting step calculate the default times.
        double[] timePoints = {0.0, 0.0707070707070707, 0.1414141414141414, 0.2121212121212121, 0.2828282828282828,
                0.35353535353535354, 0.4242424242424242, 0.4949494949494949, 0.5656565656565656, 0.6363636363636364,
                0.7070707070707071, 0.7777777777777778, 0.8484848484848484, 0.9191919191919191, 0.9898989898989898,
                1.0606060606060606, 1.1313131313131313, 1.202020202020202, 1.2727272727272727, 1.3434343434343434,
                1.4141414141414141, 1.4848484848484849, 1.5555555555555556, 1.6262626262626263, 1.6969696969696968,
                1.7676767676767675, 1.8383838383838382, 1.909090909090909, 1.9797979797979797, 2.0505050505050506,
                2.121212121212121, 2.191919191919192, 2.2626262626262625, 2.333333333333333, 2.404040404040404,
                2.4747474747474745, 2.5454545454545454, 2.616161616161616, 2.686868686868687, 2.7575757575757573,
                2.8282828282828283, 2.898989898989899, 2.9696969696969697, 3.04040404040404, 3.111111111111111,
                3.1818181818181817, 3.2525252525252526, 3.323232323232323, 3.3939393939393936, 3.4646464646464645,
                3.535353535353535, 3.606060606060606, 3.6767676767676765, 3.7474747474747474, 3.818181818181818,
                3.888888888888889, 3.9595959595959593, 4.03030303030303, 4.101010101010101, 4.171717171717171,
                4.242424242424242, 4.313131313131313, 4.383838383838384, 4.454545454545454, 4.525252525252525,
                4.595959595959596, 4.666666666666666, 4.737373737373737, 4.808080808080808, 4.878787878787879,
                4.949494949494949, 5.02020202020202, 5.090909090909091, 5.161616161616162, 5.232323232323232,
                5.303030303030303, 5.373737373737374, 5.444444444444445, 5.515151515151515, 5.585858585858586,
                5.656565656565657, 5.727272727272727, 5.797979797979798, 5.8686868686868685, 5.9393939393939394,
                6.0101010101010095, 6.08080808080808, 6.151515151515151, 6.222222222222222, 6.292929292929292,
                6.363636363636363, 6.434343434343434, 6.505050505050505, 6.575757575757575, 6.646464646464646,
                6.717171717171717, 6.787878787878787, 6.858585858585858, 6.929292929292929, 7.0};

        double[] yOut = {1.0, 1.0706501935709256, 1.140974765048918, 1.210688931991135, 1.2795483108687806,
                1.3473447285119136, 1.4139024171549208, 1.4790745602287427, 1.542740158743866, 1.6048011905907762,
                1.6651800373688195, 1.7238171554557589, 1.780668969963074, 1.8357059719994613, 1.888911001299238,
                1.9402776977747203, 1.9898091069325015, 2.0375164253625098, 2.0834178736746027, 2.1275376853284267,
                2.169905200785859, 2.2105540573184874, 2.2495214656316778, 2.2868475652277396, 2.3225748511289344,
                2.356747665221635, 2.389411746070436, 2.4206138315896917, 2.450401309453764, 2.4788219105798044,
                2.505923441431484, 2.5317535512718585, 2.5563595308412403, 2.5797881392542408, 2.6020854562014053,
                2.62329675680727, 2.643466406740311, 2.6626377753929726, 2.6808531651534517, 2.698153754976823,
                2.7145795566328093, 2.7301693821624227, 2.7449608212170067, 2.758990227082093, 2.7722927103059356,
                2.7849021389595796, 2.7968511446527513, 2.808171133518515, 2.818892301460273, 2.8290436530279752,
                2.838653023356994, 2.8477471026635475, 2.856351462845387, 2.8644905857861644, 2.872187893006914,
                2.879465776348818, 2.886345629408285, 2.892847879478632, 2.8989920197827113, 2.904796641807921,
                2.910279467579388, 2.9154573817291034, 2.920346463238455, 2.9249620167493076, 2.929318603354615,
                2.9334300707937038, 2.937309582990024, 2.940969648880431, 2.9444221504950585, 2.9476783702557783,
                2.9507490174690503, 2.9536442539959573, 2.9563737190882944, 2.9589465533849477, 2.961371422067471,
                2.963656537177827, 2.965809679104774, 2.967838217248403, 2.969749129874896, 2.97154902317578,
                2.9732441495477375, 2.974840425110601, 2.976343446482329, 2.977758506830794, 2.9790906112229294,
                2.980344491292377, 2.9815246192471365, 2.9826352212389913, 2.9836802901165527, 2.9846635975837863,
                2.985588705785755, 2.9864589783431175, 2.987277590856656, 2.9880475409027722, 2.9887716575404877,
                2.98945261035007, 2.9900929180229268, 2.9906949565219074, 2.9912609668306436, 2.99179306231};

        double[] xOut = {1.0, 1.0706501935709256, 1.140974765048918, 1.210688931991135, 1.2795483108687806,
                1.3473447285119136, 1.4139024171549208, 1.4790745602287427, 1.542740158743866, 1.6048011905907762,
                1.6651800373688195, 1.7238171554557589, 1.780668969963074, 1.8357059719994613, 1.888911001299238,
                1.9402776977747203, 1.9898091069325015, 2.0375164253625098, 2.0834178736746027, 2.1275376853284267,
                2.169905200785859, 2.2105540573184874, 2.2495214656316778, 2.2868475652277396, 2.3225748511289344,
                2.356747665221635, 2.389411746070436, 2.4206138315896917, 2.450401309453764, 2.4788219105798044,
                2.505923441431484, 2.5317535512718585, 2.5563595308412403, 2.5797881392542408, 2.6020854562014053,
                2.62329675680727, 2.643466406740311, 2.6626377753929726, 2.6808531651534517, 2.698153754976823,
                2.7145795566328093, 2.7301693821624227, 2.7449608212170067, 2.758990227082093, 2.7722927103059356,
                2.7849021389595796, 2.7968511446527513, 2.808171133518515, 2.818892301460273, 2.8290436530279752,
                2.838653023356994, 2.8477471026635475, 2.856351462845387, 2.8644905857861644, 2.872187893006914,
                2.879465776348818, 2.886345629408285, 2.892847879478632, 2.8989920197827113, 2.904796641807921,
                2.910279467579388, 2.9154573817291034, 2.920346463238455, 2.9249620167493076, 2.929318603354615,
                2.9334300707937038, 2.937309582990024, 2.940969648880431, 2.9444221504950585, 2.9476783702557783,
                2.9507490174690503, 2.9536442539959573, 2.9563737190882944, 2.9589465533849477, 2.961371422067471,
                2.963656537177827, 2.965809679104774, 2.967838217248403, 2.969749129874896, 2.97154902317578,
                2.9732441495477375, 2.974840425110601, 2.976343446482329, 2.977758506830794, 2.9790906112229294,
                2.980344491292377, 2.9815246192471365, 2.9826352212389913, 2.9836802901165527, 2.9846635975837863,
                2.985588705785755, 2.9864589783431175, 2.987277590856656, 2.9880475409027722, 2.9887716575404877,
                2.98945261035007, 2.9900929180229268, 2.9906949565219074, 2.9912609668306436, 2.99179306231};

        TransferFunction tf = new TransferFunction(new double[] {1.0, 3.0, 3.0}, new double[] {1.0, 2.0, 1.0});
        StepResponse sr = tf.step();

        assertArrayEquals(timePoints, sr.getTime(), 1e-12);
        assertArrayEquals(yOut, sr.getResponse(), 1e-12);

        // Test single point
        sr = tf.step(1);

        assertArrayEquals(new double[] {7.0}, sr.getTime(), 1e-12);
        assertArrayEquals(new double[] {1.0}, sr.getResponse(), 1e-12);

        // Test with initial conditions
        yOut = new double[] {6.0, 5.926964828289961, 5.849920761127837, 5.769755488835639, 5.687254187005841,
                5.6031093761207185, 5.517929912766562, 5.432249185059296, 5.346532579030544, 5.261184277318395,
                5.176553446529198, 5.092939865052552, 5.010599038890671, 4.929746849177359, 4.85056377148471,
                4.773198703723482, 4.697772436413435, 4.624380796312477, 4.553097491829138, 4.483976686284269,
                4.417055322918805, 4.3523552235500595, 4.289884980945544, 4.229641663298121, 4.171612347637652,
                4.1157754975914145, 4.0621021995985185, 4.0105572704830825, 3.9611002481887074, 3.9136862764648166,
                3.8682668933667146, 3.82479073257895, 3.7832041457897363, 3.7434517536270944, 3.705476932009875,
                3.669222240164042, 3.6346297960022276, 3.601641604058465, 3.5701998407064206, 3.540247100965029,
                3.5117266108068463, 3.4845824085289063, 3.4587594984206067, 3.434203979665706, 3.4108631531436155,
                3.3886856085467025, 3.367621294003365, 3.3476215701893857, 3.3286392507209426, 3.3106286304500703,
                3.2935455031260292, 3.277347169742595, 3.2619924387606134, 3.2474416192762057, 3.2336565080967112,
                3.220600371587988, 3.208237923067168, 3.1965352964336406, 3.185460016657215, 3.174980967675433,
                3.1650683581912955, 3.155693685807658, 3.146829699884785, 3.13845036346252, 3.130530814547876,
                3.123047327032134, 3.1159772714684295, 3.109299075911021, 3.102992186990586, 3.097037031375822,
                3.091414977750008, 3.086108299411801, 3.0811001375922484, 3.0763744655645087, 3.0719160536089993,
                3.0677104348844386, 3.063743872244362, 3.060003326029075, 3.0564764228545034, 3.053151425411934,
                3.050017203286064, 3.0470632047930857, 3.044279429835511, 3.0416564037661766, 3.0391851522501323,
                3.0368571771099804, 3.0346644331375336, 3.032599305852425, 3.030654590186449, 3.0288234700708836,
                3.027099498902846, 3.025476580865796, 3.0239489530785955, 3.0225111685470485, 3.0211580798915536,
                3.019884823824347, 3.01868680634983, 3.017559688661594, 3.016499373709989, 3.015501993414416};

        sr = tf.step(timePoints, new double[] {1.0, 2.0});
        // We should get back the timePoints we passed in
        assertArrayEquals(timePoints, sr.getTime(), 1e-12);
        assertArrayEquals(yOut, sr.getResponse(), 1e-12);
    }

    @Test
    public void testTimeResponse() {
        double[] timePoints = {0.0, 0.0707070707070707, 0.1414141414141414, 0.2121212121212121, 0.2828282828282828,
                0.35353535353535354, 0.4242424242424242, 0.4949494949494949, 0.5656565656565656, 0.6363636363636364,
                0.7070707070707071, 0.7777777777777778, 0.8484848484848484, 0.9191919191919191, 0.9898989898989898,
                1.0606060606060606, 1.1313131313131313, 1.202020202020202, 1.2727272727272727, 1.3434343434343434,
                1.4141414141414141, 1.4848484848484849, 1.5555555555555556, 1.6262626262626263, 1.6969696969696968,
                1.7676767676767675, 1.8383838383838382, 1.909090909090909, 1.9797979797979797, 2.0505050505050506,
                2.121212121212121, 2.191919191919192, 2.2626262626262625, 2.333333333333333, 2.404040404040404,
                2.4747474747474745, 2.5454545454545454, 2.616161616161616, 2.686868686868687, 2.7575757575757573,
                2.8282828282828283, 2.898989898989899, 2.9696969696969697, 3.04040404040404, 3.111111111111111,
                3.1818181818181817, 3.2525252525252526, 3.323232323232323, 3.3939393939393936, 3.4646464646464645,
                3.535353535353535, 3.606060606060606, 3.6767676767676765, 3.7474747474747474, 3.818181818181818,
                3.888888888888889, 3.9595959595959593, 4.03030303030303, 4.101010101010101, 4.171717171717171,
                4.242424242424242, 4.313131313131313, 4.383838383838384, 4.454545454545454, 4.525252525252525,
                4.595959595959596, 4.666666666666666, 4.737373737373737, 4.808080808080808, 4.878787878787879,
                4.949494949494949, 5.02020202020202, 5.090909090909091, 5.161616161616162, 5.232323232323232,
                5.303030303030303, 5.373737373737374, 5.444444444444445, 5.515151515151515, 5.585858585858586,
                5.656565656565657, 5.727272727272727, 5.797979797979798, 5.8686868686868685, 5.9393939393939394,
                6.0101010101010095, 6.08080808080808, 6.151515151515151, 6.222222222222222, 6.292929292929292,
                6.363636363636363, 6.434343434343434, 6.505050505050505, 6.575757575757575, 6.646464646464646,
                6.717171717171717, 6.787878787878787, 6.858585858585858, 6.929292929292929, 7.0};

        double[] yOut = {1.0, 1.0706501935709256, 1.140974765048918, 1.210688931991135, 1.2795483108687806,
                1.3473447285119136, 1.4139024171549208, 1.4790745602287427, 1.542740158743866, 1.6048011905907762,
                1.6651800373688195, 1.7238171554557589, 1.780668969963074, 1.8357059719994613, 1.888911001299238,
                1.9402776977747203, 1.9898091069325015, 2.0375164253625098, 2.0834178736746027, 2.1275376853284267,
                2.169905200785859, 2.2105540573184874, 2.2495214656316778, 2.2868475652277396, 2.3225748511289344,
                2.356747665221635, 2.389411746070436, 2.4206138315896917, 2.450401309453764, 2.4788219105798044,
                2.505923441431484, 2.5317535512718585, 2.5563595308412403, 2.5797881392542408, 2.6020854562014053,
                2.62329675680727, 2.643466406740311, 2.6626377753929726, 2.6808531651534517, 2.698153754976823,
                2.7145795566328093, 2.7301693821624227, 2.7449608212170067, 2.758990227082093, 2.7722927103059356,
                2.7849021389595796, 2.7968511446527513, 2.808171133518515, 2.818892301460273, 2.8290436530279752,
                2.838653023356994, 2.8477471026635475, 2.856351462845387, 2.8644905857861644, 2.872187893006914,
                2.879465776348818, 2.886345629408285, 2.892847879478632, 2.8989920197827113, 2.904796641807921,
                2.910279467579388, 2.9154573817291034, 2.920346463238455, 2.9249620167493076, 2.929318603354615,
                2.9334300707937038, 2.937309582990024, 2.940969648880431, 2.9444221504950585, 2.9476783702557783,
                2.9507490174690503, 2.9536442539959573, 2.9563737190882944, 2.9589465533849477, 2.961371422067471,
                2.963656537177827, 2.965809679104774, 2.967838217248403, 2.969749129874896, 2.97154902317578,
                2.9732441495477375, 2.974840425110601, 2.976343446482329, 2.977758506830794, 2.9790906112229294,
                2.980344491292377, 2.9815246192471365, 2.9826352212389913, 2.9836802901165527, 2.9846635975837863,
                2.985588705785755, 2.9864589783431175, 2.987277590856656, 2.9880475409027722, 2.9887716575404877,
                2.98945261035007, 2.9900929180229268, 2.9906949565219074, 2.9912609668306436, 2.99179306231};

        double[][] xOut = {
                {
                        0.0, 0.0658802372927001, 0.12276581740242994, 0.17157777364468568, 0.21315330661802717,
                        0.24825293777718033, 0.2775670769978888, 0.30172205080482417, 0.3212856342926908,
                        0.3367721264091228, 0.34864700516486036, 0.35733119647238726, 0.3632049876705734,
                        0.3666116143531105, 0.3678605468671438, 0.36723050077113617, 0.3649721936243862,
                        0.3613108687124959, 0.35644860468209855, 0.35056642855383413, 0.3438262481952057,
                        0.33637261905560045, 0.3283343587861405, 0.31982602227948115, 0.3109492486621279,
                        0.30179399084775776, 0.2924396374083694, 0.28295603573526606, 0.27340442473873555,
                        0.2638382846690755, 0.2543041110279115, 0.24484211897350872, 0.23548688410322646,
                        0.22626792501694573, 0.21721023262400183, 0.20833475074992816, 0.19965881222542425,
                        0.19119653429588468, 0.18295917687323482, 0.17495546686056576, 0.16719189151215996,
                        0.15967296354512095, 0.15240146049227438, 0.14537864057773328, 0.13860443720508037,
                        0.13207763397218572, 0.12579602196502493, 0.11975654093435925, 0.11395540582274719,
                        0.10838821998410941, 0.1030500763230853, 0.09793564747587775, 0.09303926605743365,
                        0.08835499591095157, 0.08387669521421012, 0.07959807222247597, 0.07551273435923819,
                        0.07161423130322324, 0.06789609266261158, 0.06435186077467574, 0.0609751191208013,
                        0.05775951680267348, 0.05469878948498206, 0.05178677717301004, 0.049017439159644814,
                        0.04638486644542565, 0.04388329190697991, 0.04150709846338223, 0.03925082546639474,
                        0.03710917351902635, 0.03507700790721384, 0.0331493608115207, 0.03132143244942642,
                        0.029588591283906195, 0.02794637342045768, 0.026390481302404463, 0.024916781803092716,
                        0.023521303803403573, 0.02220023533374192, 0.02094992035125299, 0.019766855215387902,
                        0.01864768491802142, 0.0175891991180573, 0.016588328024782814, 0.015642138169102163,
                        0.014747828097140758, 0.0139027240165258, 0.013104275421872633, 0.012350050722605012,
                        0.011637732893176309, 0.010965115163008222, 0.010330096760995132, 0.009730678727209403,
                        0.009164959802464095, 0.008631132404621142, 0.008127478698956796, 0.0076523667684937,
                        0.007204246888963588, 0.006781647911962285, 0.006383173758884669
                },
                {
                        0.0, 0.0023849781391127975, 0.00910447382324402, 0.01955557917322463, 0.03319750212537667,
                        0.04954589536736659, 0.068167670078516, 0.08867625471195933, 0.11072726222558762,
                        0.1340145320908267, 0.15826651610197962, 0.18324297949168583, 0.20873199114625032,
                        0.23454717882317538, 0.2605252272160471, 0.28652359850179204, 0.3124184566540576,
                        0.3381027783250069, 0.363484634496252, 0.3884856283872963, 0.4130394762953268,
                        0.43709071913144354, 0.46059355342276864, 0.48351077147412924, 0.5058128012334032,
                        0.5274768371869386, 0.5484860543310333, 0.5688288979272127, 0.5884984423575144,
                        0.6074918129553645, 0.6258096652017862, 0.643455716149175, 0.6604363233690069,
                        0.6767601071186476, 0.6924376117887019, 0.7074810030286708, 0.7219037972574432,
                        0.7357206205485439, 0.7489469941401085, 0.7615991440581285, 0.7736938325603246,
                        0.7852482093086509, 0.7962796803623662, 0.8068057932521798, 0.8168441365504276,
                        0.8264122524936969, 0.8355275613438631, 0.8442072962920779, 0.852468447818763,
                        0.8603277165219329, 0.8678014735169544, 0.8749057275938349, 0.8816560983939767,
                        0.8880677949376066, 0.8941555988963519, 0.899933852063171, 0.9054164475245235,
                        0.9106168240877042, 0.91554796356005, 0.9202223905166227, 0.9246521742292935,
                        0.9288489324632151, 0.9328238368767365, 0.9365876197881489, 0.9401505820974851,
                        0.943522602174139, 0.9467131455415223, 0.9497312752085243, 0.952585662514332,
                        0.955284598368376, 0.9578360047809182, 0.9602474465922184, 0.962526143319434,
                        0.9646789810505206, 0.9667125243235066, 0.9686330279377112, 0.9704464486508406,
                        0.9721584567224995, 0.9737744472705772, 0.9752995514122634, 0.9767386471661749,
                        0.9780963700962897, 0.9793771236821358, 0.9805850894030055, 0.9817242365269138,
                        0.9827983315976181, 0.9838109476153054, 0.9847654729085593, 0.9856651196969737,
                        0.9865129323453051, 0.9873117953113736, 0.988064440791061, 0.9887734560647231,
                        0.989441290550154, 0.9900702625679333, 0.9906625658255568, 0.9912202756272166,
                        0.991745354816472, 0.9922396594593407, 0.9927049442755577
                }
        };

        TransferFunction tf = new TransferFunction(new double[] {1.0, 3.0, 3.0}, new double[] {1.0, 2.0, 1.0});
        SingleInputSingleOutputTimeResponse SISOtr = tf.simulateTimeResponse(NumArrays.ones(timePoints.length),
                timePoints);

        double[][] transposedStateVector = NumArrays.transpose(SISOtr.getEvolutionOfStateVector());

        assertArrayEquals(timePoints, SISOtr.getTime(), 1e-12);
        assertArrayEquals(yOut, SISOtr.getResponse(), 1e-12);
        assertArrayEquals(xOut[0], transposedStateVector[0], 1e-12);
        assertArrayEquals(xOut[1], transposedStateVector[1], 1e-12);

        // TODO test with inital conditions
    }
}