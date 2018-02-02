package com.wildbitsfoundry.etk4j.math.solvers.univariate;

import com.wildbitsfoundry.etk4j.math.calculus.Derivatives;
import com.wildbitsfoundry.etk4j.math.functions.UnivariateFunction;
import com.wildbitsfoundry.etk4j.math.solvers.univariate.UnivariateSolverResults.SolverStatus;


public class NewtonRaphson {
	
	protected int _maxIter = 100;
	protected double _absTol = 1e-9;
	protected double _relTol = 1e-6;
	protected double _maxVal = Double.NaN;
	protected double _step = 0.001;
	protected double _x0;
	
	protected UnivariateFunction _func;
	protected UnivariateFunction _derivative;


	public NewtonRaphson(UnivariateFunction func, double initialGuess) {
		_func = func;
		_x0 = initialGuess;
	}

	public NewtonRaphson(UnivariateFunction func, UnivariateFunction derivative, double intialGuess) {
		_func = func;
		_derivative = derivative;
		_x0 = intialGuess;
	}

	public NewtonRaphson iterationLimit(int limit) {
		_maxIter = limit;
		return this;
	}

	public NewtonRaphson absTolerance(double tol) {
		_absTol = tol;
		return this;
	}

	public NewtonRaphson relTolerance(double tol) {
		_relTol = tol;
		return this;
	}

	public NewtonRaphson maxAbsAllowedValue(double max) {
		_maxVal = max;
		return this;
	}

	public NewtonRaphson differentiationStepSize(double step) {
		_step = step;
		return this;
	}
	
	protected static UnivariateSolverResults buildResults(double xfinal, SolverStatus status, int iterCount, double error) {
		UnivariateSolverResults sr = new UnivariateSolverResults();
		sr.Iterations = iterCount;
		sr.Solution = xfinal;
		sr.Status = status;
		sr.Converged = status.equals(SolverStatus.SUCCESS) ? true : false;
		sr.EstimatedError = error;
		return sr;
	}
	
	public UnivariateSolverResults solve() {
		return this.solve(_derivative);
	}

	protected UnivariateSolverResults solve(UnivariateFunction derivative) {
		int maxiter = _maxIter;
		double maxval = _maxVal;
		double step = _step;
		UnivariateFunction func = _func;

		double xcurrent = _x0;
		double xfinal = 0.0;
		double error = 0.0;
		while (maxiter-- > 0) {
			if (derivative != null) {
				xfinal = xcurrent - func.evaluateAt(xcurrent) / derivative.evaluateAt(xcurrent); 
			} else {
				double fprime = Derivatives.centeredDifference(func, xcurrent, step);
				xfinal = xcurrent - func.evaluateAt(xcurrent) / fprime;
			}

			error = Math.abs(xfinal - xcurrent);
			if (error < _absTol + _relTol * Math.min(Math.abs(xfinal), Math.abs(xcurrent))) {
				return buildResults(xfinal, SolverStatus.SUCCESS, _maxIter - maxiter, error);
			}

			if (!Double.isNaN(maxval) && Double.compare(xfinal, maxval) >= 0) {
				return buildResults(xfinal, SolverStatus.MAX_ABS_VALUE_EXCEEDED, _maxIter - maxiter, error);
			}
			xcurrent = xfinal;
		}
		return buildResults(xfinal, SolverStatus.ITERATION_LIMIT_EXCEEDED, _maxIter - maxiter, error);
	}

	public static void main(String[] args) {

		System.out.printf("Solution with pre-computed Jacobian%n------------------------------------%n");
		UnivariateSolverResults sr1 = new NewtonRaphson(x -> 2 - x * x, x -> -2 * x, 1)
				.absTolerance(1e-9)
				.relTolerance(1e-6)
				.iterationLimit(100)
				.differentiationStepSize(1e-7)
				.solve();
		System.out.println(sr1);

		System.out.printf("%nSolution with auto-computed Jacobian%n------------------------------------%n");
		UnivariateSolverResults sr2 = new NewtonRaphson(x -> 2 - x * x, 1)
				.absTolerance(1e-9)
				.relTolerance(1e-6)
				.iterationLimit(100)
				.differentiationStepSize(1e-7)
				.solve();
		System.out.println(sr2);
	}
}