package com.wildbitsfoundry.etk4j.signals.filters;

import com.wildbitsfoundry.etk4j.control.TransferFunction;
import com.wildbitsfoundry.etk4j.control.ZeroPoleGain;
import com.wildbitsfoundry.etk4j.math.complex.Complex;
import com.wildbitsfoundry.etk4j.math.polynomials.RationalFunction;
import com.wildbitsfoundry.etk4j.util.Tuples;

import static com.wildbitsfoundry.etk4j.math.optimize.minimizers.GoldenSection.goldenSectionMinimizer;

import java.util.Arrays;
import java.util.function.Function;

import static com.wildbitsfoundry.etk4j.signals.filters.FilterSpecs.*;

public final class ButterWorth extends Filter {

    private static FilterOrderCalculationStrategy strategy = new ButterworthOrderCalculationStrategy();

    public static ZeroPoleGain buttAp(int n) {
        final double pid = Math.PI / 180.0;
        final double nInv = 1.0 / n;
        Complex[] poles = new Complex[n];
        if (n % 2 == 0) {
            for (int k = (-n >> 1) + 1, i = 0; k <= n >> 1; ++k, ++i) {
                double phik = nInv * (180.0 * k - 90.0);
                poles[n - i - 1] = Complex.newComplex(-Math.cos(phik * pid), Math.sin(phik * pid));
            }
        } else {
            for (int k = -(n - 1) >> 1, i = 0; k <= (n - 1) >> 1; ++k, ++i) {
                double phik = nInv * 180.0 * k;
                poles[n - i - 1] = Complex.newComplex(-Math.cos(phik * pid), Math.sin(phik * pid));
            }
        }
        Complex[] zeros = new Complex[0];
        double k = RationalFunction.calculateGain(zeros, poles);
        return new ZeroPoleGain(zeros, poles, k);
    }

    public static Tuples.Tuple2<Integer, Double> buttOrd(LowPassSpecs specs) {
        // TODO validate inputs
        return lowPassFilterOrder(specs, new ButterworthOrderCalculationStrategy());
    }

    public static Tuples.Tuple2<Integer, Double> buttOrd(HighPassSpecs specs) {
        return highPassFilterOrder(specs, new ButterworthOrderCalculationStrategy());
    }

    public static Tuples.Tuple3<Integer, Double, Double> buttOrd(BandPassSpecs specs) {
        return bandPassFilterOrder(specs, new ButterworthOrderCalculationStrategy());
    }

    public static Tuples.Tuple3<Integer, Double, Double> buttOrd(BandStopSpecs specs) {
        return bandStopFilterOrder(specs, new ButterworthOrderCalculationStrategy());
    }

    // TODO change this to return an AnalogFilter?
    public static TransferFunction newLowPass(int n, double wn) {
        ZeroPoleGain zpk = buttAp(n);
        return AnalogFilter.lpTolp(zpk, wn);
    }

    public static TransferFunction newHighPass(int n, double wn) {
        ZeroPoleGain zpk = buttAp(n);
        return AnalogFilter.lpTohp(zpk, wn);
    }

    // TODO create exceptions. add checks to other filters
    public static TransferFunction newBandPass(int n, double wp1, double wp2) {
        if(n <= 0) {
            // throw
        }
        if(wp1 <= 0 || wp2 <= 0) {
           // throw
        }
        if(wp1 <= wp2) {
            // throw
        }
        ZeroPoleGain zpk = buttAp(n);
        double w0 = Math.sqrt(wp1 * wp2);
        double bw = wp2 - wp1;
        return AnalogFilter.lpTobp(zpk, w0, bw);
    }

    public static TransferFunction newBandStop(int n, double wp1, double wp2) {
        ZeroPoleGain zpk = buttAp(n);
        double w0 = Math.sqrt(wp1 * wp2);
        double bw = wp2 - wp1;
        return AnalogFilter.lpTobs(zpk, w0, bw);
    }

    private static void checkInputsLowPassHighPass(int n, double wn) {
        if (n <= 0) {
            // throw
        }
        if (wn <= 0) {
            // throw
        }
    }

    public static void main(String[] args) {
        ZeroPoleGain zpk = buttAp(5);
        LowPassSpecs lpSpecs = new LowPassSpecs();
        lpSpecs.setPassBandRipple(1.5); // 1.5 dB gain/ripple refer to note
        lpSpecs.setStopBandAttenuation(60.0); // 60 dB at the stop band
        lpSpecs.setPassBandFrequency(2500); // 2500 Hz cutoff frequency
        lpSpecs.setStopBandFrequency(10000); // 10000 Hz stop band frequency
        Tuples.Tuple2<Integer, Double> buttord = buttOrd(lpSpecs);
        System.out.println(buttord.getItem1());
        System.out.println(buttord.getItem2());

        TransferFunction tf = newLowPass(buttord.getItem1(), buttord.getItem2());
        System.out.println(tf);

        HighPassSpecs hpSpecs = new HighPassSpecs();
        hpSpecs.setPassBandRipple(0.2); // 0.2 dB gain/ripple refer to note
        hpSpecs.setStopBandAttenuation(60.0); // 60 dB at the stop band
        hpSpecs.setPassBandFrequency(12); // 12 Hz cutoff frequency
        hpSpecs.setStopBandFrequency(0.2); // 0.2 Hz stop band frequency
        buttord = buttOrd(hpSpecs);
        System.out.println(buttord.getItem1());
        System.out.println(buttord.getItem2());

        tf = newHighPass(buttord.getItem1(), buttord.getItem2());
        System.out.println(tf);

        BandPassSpecs bpSpecs = new BandPassSpecs();
        // The bandwidth of the filter starts at the LowerPassBandFrequency and
        // ends at the UpperPassBandFrequency. The filter has lower stop band
        // which is set LowerStopBandFrequency and the upper stop band can be set
        // with UpperStopBandFrequency. The attenuation at the stop bands can be
        // set with the LowerStopBandAttenuation and UpperStopBandAttenuation
        // respectively. In a frequency spectrum, the order of the frequencies will be:
        // LowerStopBandFrequency < LowerPassBandFrequency < UpperPassBandFrequency <
        // UpperStopBandFrequency
        bpSpecs.setLowerPassBandFrequency(190.0); // 190 Hz lower pass band frequency
        bpSpecs.setUpperPassBandFrequency(210.0); // 210 Hz upper pass band frequency
        bpSpecs.setLowerStopBandFrequency(180.0); // 180 Hz lower stop band frequency
        bpSpecs.setUpperStopBandFrequency(220.0); // 220 Hz upper stop band frequency
        bpSpecs.setPassBandRipple(0.2); // 0.2 dB gain/ripple refer to note
        bpSpecs.setLowerStopBandAttenuation(20.0); // 20 dB attenuation at the lower end of the skirt
        bpSpecs.setUpperStopBandAttenuation(20.0); // 20 dB attenuation at the upper end of the skirt
        bpSpecs.setStopBandAttenuation(20);
        Tuples.Tuple3<Integer, Double, Double> buttordBP = buttOrd(bpSpecs);

        tf = newBandPass(buttordBP.getItem1(), buttordBP.getItem2(), buttordBP.getItem3());
        System.out.println(tf);

        BandStopSpecs bsSpecs = new BandStopSpecs();
        // The notch of the filter starts at the LowerStopBandFrequency and
        // ends at the UpperStopBandFrequency. The filter has lower pass band
        // which is set LowerPassBandFrequency and the upper pass band can be set
        // with UpperPassBandFrequency. The attenuation at the notch can be
        // set with the StopBandAttenuation parameter and the attenuation/ripple
        // in the pass band can be set with the PassBandRipple parameter.
        // In a frequency spectrum, the order of the frequencies will be:
        // LowerPassBandFrequency < LowerStopBandFrequency < UpperStopBandFrequency <
        // UpperPassBandFrequency
        bsSpecs.setLowerPassBandFrequency(3.6e3); // 3600 Hz lower pass band frequency
        bsSpecs.setUpperPassBandFrequency(9.1e3); // 9100 Hz lower pass band frequency
        bsSpecs.setLowerStopBandFrequency(5.45e3); // 5450 Hz lower stop band frequency
        bsSpecs.setUpperStopBandFrequency(5.90e3); // 5900 Hz upper stop band frequency
        bsSpecs.setPassBandRipple(0.5); // 1.5 dB gain/ripple refer to note
        bsSpecs.setStopBandAttenuation(38.0); // 38 db attenuation at the notch
        Tuples.Tuple3<Integer, Double, Double> buttordBS = buttOrd(bsSpecs);

        tf = newBandStop(buttordBS.getItem1(), buttordBS.getItem2(), buttordBS.getItem3());
        System.out.println(tf);
    }
}
