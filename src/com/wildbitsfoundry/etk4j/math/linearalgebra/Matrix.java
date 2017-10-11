package com.wildbitsfoundry.etk4j.math.linearalgebra;

import java.util.Arrays;

import com.wildbitsfoundry.etk4j.math.MathETK;

public class Matrix {
	private double[] _data;
	private int _rows;
	private int _cols;

	public Matrix(int rows, int cols) {
		_rows = rows;
		_cols = cols;

		this._data = new double[rows * cols];
	}

	public Matrix(double[] data, int rows) {
		_rows = rows;
		_cols = data.length / rows;
		int dim = _rows * _cols;
		_data = new double[dim];
		System.arraycopy(data, 0, _data, 0, dim);

	}

	public Matrix(double[][] data) {
		_rows = data.length;
		_cols = data[0].length;
		_data = Matrix.arrayflatten(data);
	}

	public Matrix(double[][] data, int rows, int cols) {
		_rows = rows;
		_cols = cols;
		_data = Matrix.arrayflatten(data);
	}

	public Matrix(double[] data, int rows, int cols) {
		_rows = rows;
		_cols = cols;
		_data = data;
	}

	public Matrix(Matrix matrix) {
		_rows = matrix._rows;
		_cols = matrix._cols;
		_data = new double[_rows * _cols];
		System.arraycopy(matrix._data, 0, this._data, 0, this._rows * this._cols);
	}

	public Matrix(int rows, int cols, double val) {
		_rows = rows;
		_cols = cols;
		_data = new double[_rows * _cols];
		Arrays.fill(_data, val);
	}

	/**
	 * Helper method to copy a 2 dimensional array
	 * 
	 * @param array
	 *            to copy
	 * @return a newly created array containing the copy of array
	 */
	protected static double[][] arraycopy(final double[][] array) {
		int rows = array.length;
		int cols = array[0].length;
		double[][] copy = new double[rows][cols];
		for (int i = 0; i < rows; i++) {
			System.arraycopy(array[i], 0, copy[i], 0, cols);
		}
		return copy;
	}

	/**
	 * Helper method to copy a 2 dimensional array
	 * 
	 * @param array
	 *            to copy
	 * @return a newly created array containing the copy of array
	 */
	protected static double[] arrayflatten(final double[][] array) {
		int rows = array.length;
		int cols = array[0].length;
		double[] copy = new double[rows * cols];
		for (int i = 0; i < rows; i++) {
			System.arraycopy(array[i], 0, copy, i * cols, cols);
		}
		return copy;
	}

	/***
	 * Get submatrix
	 * 
	 * @param row0
	 *            Initial row index
	 * @param row1
	 *            Final row index
	 * @param col0
	 *            Initial column index
	 * @param col1
	 *            Final column index
	 * @return A(row0 : row1, col0 : col1)
	 */
	public Matrix subMatrix(int row0, int row1, int col0, int col1) {
		int rowDim = row1 - row0 + 1;
		int colDim = col1 - col0 + 1;
		double[] data = new double[rowDim * colDim];
		for (int i = 0; i < rowDim; ++i) {
			for (int j = 0; j < colDim; ++j) {
				data[i * colDim + j] = _data[(i + row0) * _cols + (j + col0)];
			}
		}
		return new Matrix(data, rowDim, colDim);
	}

	/***
	 * Get submatrix
	 * 
	 * @param rows
	 *            Array of row indices
	 * @param col0
	 *            Initial column index
	 * @param col1
	 *            Final column index
	 * @return A(rows(:), col0 : col1)
	 */
	public Matrix subMatrix(int[] rows, int col0, int col1) {
		int rowDim = rows.length;
		int colDim = col1 - col0 + 1;
		double[] data = new double[rowDim * colDim];
		for (int i = 0; i < rowDim; ++i) {
			for (int j = 0; j < colDim; ++j) {
				data[i * colDim + j] = _data[rows[i] * _cols + (j + col0)];
			}
		}
		return new Matrix(data, rowDim, colDim);
	}

	/***
	 * Get submatrix
	 * 
	 * @param row0
	 *            Initial row index
	 * @param row1
	 *            Final row index
	 * @param cols
	 *            Array of column indices
	 * @return A(row0 : row1, cols(:))
	 */
	public Matrix subMatrix(int row0, int row1, int[] cols) {
		int rowDim = row1 - row0 + 1;
		int colDim = cols.length;
		;
		double[] data = new double[rowDim * colDim];
		for (int i = 0; i < rowDim; ++i) {
			for (int j = 0; j < colDim; ++j) {
				data[i * colDim + j] = _data[(i + row0) * _cols + cols[j]];
			}
		}
		return new Matrix(data, rowDim, colDim);
	}

	/***
	 * Get submatrix
	 * 
	 * @param rows
	 *            Array or row indices
	 * @param cols
	 *            Array of column indices
	 * @return A(rows(:), cols(:))
	 */
	public Matrix subMatrix(int[] rows, int[] cols) {
		int rowDim = rows.length;
		int colDim = cols.length;
		double[] data = new double[rowDim * colDim];
		for (int i = 0; i < rowDim; ++i) {
			for (int j = 0; j < colDim; ++j) {
				data[i * colDim + j] = _data[rows[i] * _cols + cols[j]];
			}
		}
		return new Matrix(data, rowDim, colDim);
	}

	public double get(int i, int j) {
		return _data[i * _cols + j];
	}

	public void set(int i, int j, double val) {
		_data[i * _cols + j] = val;
	}

	public double det() {
		return new LUDecomposition(this).det();
	}

	/**
	 * Matrix rank
	 * 
	 * @return effective numerical rank, obtained from SVD.
	 */

	public int rank() {
		return new SingularValueDecomposition(this).rank();
	}

	/**
	 * Matrix condition (2 norm)
	 * 
	 * @return ratio of largest to smallest singular value.
	 */

	public double cond() {
		return new SingularValueDecomposition(this).cond();
	}

	public Matrix cofactor() {
		int dim = this._rows;
		// Make sure that matrix is square

		double[][] cofactor = new double[dim][dim];

		int sign = -1;
		for (int i = 0; i < dim; ++i) {
			for (int j = 0; j < dim; ++j) {
				sign = -sign;
				cofactor[i][j] = sign * this.minor(i, j).det();
			}
		}
		return new Matrix(cofactor);
	}

	public boolean isSquared() {
		return _rows == _cols;
	}

	public Matrix minor(int row, int col) {
		final int dim = this._rows;
		double[][] minor = new double[dim - 1][dim - 1];

		for (int i = 0; i < dim; ++i) {
			int offset = i * this._cols;
			for (int j = 0; i != row && j < this._cols; ++j) {
				if (j != col) {
					minor[i < row ? i : i - 1][j < col ? j : j - 1] = this._data[offset + j];
				}
			}
		}
		return new Matrix(minor);
	}

	public Matrix adjoint() {
		return this.cofactor().transpose();
	}

	public LUDecomposition LU() {
		return new LUDecomposition(this);

	}

	public QRDecomposition QR() {
		return new QRDecomposition(this);
	}
	
	public CholeskyDecomposition Chol() {
		return new CholeskyDecomposition(this);
	}

	public SingularValueDecomposition SVD() {
		return new SingularValueDecomposition(this);
	}

	/***
	 * Get matrix diagonal
	 * 
	 * @return Array containing the diagonal of the matrix
	 */
	public double[] diag() {
		if (!this.isSquared()) {
			throw new RuntimeException("Matrix is not squared");
		}
		final int dim = _rows;
		double[] diag = new double[dim];
		for (int i = 0; i < dim; ++i) {
			diag[i] = _data[i * dim + i];
		}
		return diag;
	}

	public Matrix inv() {
		return this.solve(Matrices.Identity(_rows));
	}

	public Matrix transpose() {
		double[] result = new double[_rows * _cols];
		final int trows = _cols;
		final int tcols = _rows;

		for (int i = 0; i < _rows; ++i) {
			for (int j = 0; j < _cols; ++j) {
				result[j * tcols + i] = _data[i * _cols + j];
			}
		}
		return new Matrix(result, trows, tcols);
	}

	/***
	 * One norm
	 * 
	 * @return maximum column sum
	 */
	public double norm1() {
		double norm = 0.0;
		for (int j = 0; j < _cols; ++j) {
			double sum = 0.0;
			for (int i = 0; i < _rows; ++i) {
				sum += _data[i * _cols + j];
			}
			norm = Math.max(norm, sum);
		}
		return norm;
	}

	/**
	 * Two norm
	 * 
	 * @return maximum singular value.
	 */

	public double norm2() {
		return new SingularValueDecomposition(this).norm2();
	}

	/***
	 * Infinity norm
	 * 
	 * @return maximum row sum
	 */
	public double normInf() {
		double norm = 0.0;
		for (int i = 0; i < _rows; ++i) {
			double sum = 0.0;
			for (int j = 0; j < _cols; ++j) {
				sum += _data[i * _cols + j];
			}
			norm = Math.max(norm, sum);
		}
		return norm;
	}

	/***
	 * Frobenius norm
	 * 
	 * @return square root of the sum of squares of all elements
	 */
	public double normFrob() {
		double norm = 0.0;
		for (int i = 0; i < _rows; ++i) {
			for (int j = 0; j < _cols; ++j) {
				norm = MathETK.hypot(norm, _data[i * _cols + j]);
			}
		}
		return norm;
	}

	public Matrix add(Matrix mat) {
		double[] result = new double[this._rows * this._cols];
		for (int i = 0; i < this._rows * this._cols; ++i) {
			result[i] = this._data[i] + mat._data[i];
		}
		return new Matrix(result, _rows, _cols);
	}

	public Matrix subtract(Matrix mat) {
		double[] result = new double[this._rows * this._cols];
		for (int i = 0; i < this._rows * this._cols; ++i) {
			result[i] = this._data[i] - mat._data[i];
		}
		return new Matrix(result, _rows, _cols);
	}

	public Matrix multiply(Matrix mat) {
		double[] result = new double[this._rows * mat._cols];
		for (int i = 0, j = 0; i < this._rows; i++) {
			for (int k = 0; k < mat._cols; k++, j++) {
				double value = this._data[i * this._cols] * mat._data[k];
				for (int l = 1, m = i * this._cols + 1, n = k + mat._cols; l < this._cols; l++, m++, n += mat._cols) {
					value += this._data[m] * mat._data[n];
				}
				result[j] = value;
			}
		}
		return new Matrix(result, _rows, mat._cols);
	}

	public Matrix solve(Matrix B) {

		if (_rows == _cols) { // Matrix is Squared
			return new LUDecomposition(this).solve(B);
		} else if (_rows > _cols) { // Matrix is thin (Overdetermined system)
			return new QRDecomposition(this).solve(B);
		} else { // Matrix is fat (Underdetermined system)
			QRDecomposition qr = this.transpose().QR();
			Matrix R1 = Matrices.fwdSubsSolve(qr.getRT(), B);
			R1.appendRows(_cols - R1._rows);
			return qr.QmultiplyX(R1);
		}
	}

	public void appendRows(int count) {
		_rows += count;
		final int newSize = _rows * _cols;
		_data = Arrays.copyOf(_data, newSize);
		
	}

	public Matrix pinv() {
		double eps = 2e-16;
		int rows = _rows;
		int cols = _cols;

		if (rows < cols) {
			Matrix result = this.transpose().pinv();
			if (result != null) {
				result = result.transpose();
			}
			return result;
		}

		SingularValueDecomposition svdX = this.SVD();
		if (svdX.rank() < 1) {
			return null;
		}

		double[] singularValues = svdX.getSingularValues();
		double tol = Math.max(rows, cols) * singularValues[0] * eps;
		double[] singularValueReciprocals = new double[singularValues.length];
		for (int i = 0; i < singularValues.length; i++) {
			if (Math.abs(singularValues[i]) >= tol) {
				singularValueReciprocals[i] = 1.0 / singularValues[i];
			}
		}
		Matrix U = svdX.getU();
		Matrix V = svdX.getV();
		int min = Math.min(cols, U._cols);
		double[][] inverse = new double[cols][rows];
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < U._rows; j++) {
				for (int k = 0; k < min; k++) {
					inverse[i][j] += V.get(i, k) * singularValueReciprocals[k] * U.get(j, k);
				}
			}
		}
		return new Matrix(inverse);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < _rows * _cols; ++i) {
			if (i > 0 && i % _cols == 0) {
				sb.append(System.lineSeparator());
			}
			sb.append(String.format("%.4f", _data[i])).append(" ");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		double[][] aa = new double[][] { { 1, -1, 4 }, { 1, 4, -2 }, { 1, 4, 2 }, { 1, -1, 0 } };
		QRDecomposition qr = new Matrix(aa).QR();
		System.out.printf("H : %n%s%n%n", qr.getH());
		System.out.printf("R : %n%s%n%n", qr.getR());
		// System.out.printf("Q : %n%s%n%n", qr.getQ());
		System.out.printf("Q : %n%s%n%n", qr.getQ());
		System.out.printf("transpose(Q) : %n%s%n%n", qr.getQT());
		
		System.out.printf("A.inv() : %n%s%n%n", new Matrix(aa).transpose().inv());
		System.out.printf("A.pinv() : %n%s%n%n", new Matrix(aa).transpose().pinv());

		double[][] original = new double[][] { { 1, 2, 3 }, { 0, 4, 5 }, { 1, 0, 6 } };
		Matrix sol = new Matrix(new double[] { 6, 4, 2 }, 3);

		Matrix A = new Matrix(original);
		System.out.printf("A : %n%s%n%n", A);
		System.out.printf("A.transpose() : %n%s%n%n", A.transpose());
		System.out.printf("A.subMatrix(0, 2, 1, 2).transpose() : %n%s%n%n", A.subMatrix(0, 2, 1, 2).transpose());
		System.out.printf("A.subMatrix(1, 2, 0, 2).transpose() : %n%s%n%n", A.subMatrix(1, 2, 0, 2).transpose());
		System.out.printf("A.cofactor() : %n%s%n%n", A.cofactor());
		System.out.printf("A.adjoint() : %n%s%n%n", A.adjoint());
		System.out.printf("A.inv() : %n%s%n%n", A.inv());
		System.out.printf("A.pinv() : %n%s%n%n", A.pinv());
		System.out.printf("A.det() : %n%,4f%n%n", A.det());
		System.out.printf("A.multiply(A) : %n%s%n%n", A.multiply(A));
		System.out.printf("A.add(A) : %n%s%n%n", A.add(A));
		System.out.printf("A.norm1() : %n%.4f%n%n", A.norm1());
		System.out.printf("A.normInf() : %n%.4f%n%n", A.normInf());
		System.out.printf("A.normFrob() : %n%.4f%n%n", A.normFrob());
		System.out.printf("A.subMatrix(1, 2, 1, 2) : %n%s%n%n", A.subMatrix(1, 2, 1, 2));
		System.out.printf("A.subMatrix([1, 2], 1, 2) : %n%s%n%n", A.subMatrix(new int[] { 1, 2 }, 1, 2));
		System.out.printf("A.subMatrix(1, 2, [1, 2]) : %n%s%n%n", A.subMatrix(1, 2, new int[] { 1, 2 }));
		System.out.printf("A.subMatrix([1, 2], [1, 2]) : %n%s%n%n",
				A.subMatrix(new int[] { 1, 2 }, new int[] { 1, 2 }));
		System.out.printf("A.subMatrix([0, 2], [1, 2]) : %n%s%n%n",
				A.subMatrix(new int[] { 0, 2 }, new int[] { 1, 2 }));
		System.out.printf("A.solve([6, 4, 2]) : %n%s%n%n", A.solve(sol));
		System.out.printf("A(:, 1:2).solve([6, 4, 2]) : %n%s%n%n", A.subMatrix(0, 2, 1, 2).solve(sol));
		System.out.printf("A.eig().getD().diag() : %n%s%n%n", Arrays.toString(A.eig().getD().diag()));

	}

	public EigenvalueDecomposition eig() {
		return this.eig(true);
	}

	public EigenvalueDecomposition eig(boolean balance) {
		Matrix A = balance ? new Matrix(this) : this;
		return new EigenvalueDecomposition(A, balance);
	}

	public int getRowCount() {
		return _rows;
	}

	public int getColumnCount() {
		return _cols;
	}

	public double[] getRow(int row) {
		double[] result = new double[_cols];
		int rowIndex = row * _cols;
		for (int j = 0; j < _cols; ++j) {
			result[j] = _data[rowIndex + j];
		}
		return result;
	}

	public double[] getArrayCopy() {
		return Arrays.copyOf(_data, _data.length);
	}

	public double[] getArray() {
		return _data;
	}

}