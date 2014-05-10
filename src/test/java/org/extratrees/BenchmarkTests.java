package org.extratrees;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BenchmarkTests {

	public static FactorExtraTrees getSampleData(int ndata, int ndim) {
		int[] output = new int[ndata];
		double[] v   = new double[ndata*ndim];
		for (int i=0; i<v.length; i++) {
			v[i] = Math.random();
		}
		Matrix m = new Matrix(v, ndata, ndim);
		// generate values for all outputs
		for (int row=0; row<output.length; row++) {
			m.set(row, 2, 0.5);
			output[row] = (int) Math.floor(m.get(row, 1)+2*m.get(row, 3));
		}
		FactorExtraTrees et = new FactorExtraTrees(m, output);
		return et;
	}
	
	public static ExtraTrees getSampleDataRegression(int ndata, int ndim) {
		double[] output = new double[ndata];
		double[] v   = new double[ndata*ndim];
		for (int i=0; i<v.length; i++) {
			v[i] = Math.random();
		}
		Matrix m = new Matrix(v, ndata, ndim);
		// generate values for all outputs
		for (int row=0; row<output.length; row++) {
			m.set(row, 2, 0.5);
			output[row] = m.get(row, 1)+2*m.get(row, 3);
		}
		ExtraTrees et = new ExtraTrees(m, output);
		return et;
	}


	@Test
	public void testFactor() {
		int ndata = 1000;
		int nTrees = 100;
		int inputDim = 800;
		FactorExtraTrees et = getSampleData(ndata, inputDim);
		assertEquals( false, et.isHasNaN() );
		
		Timer.tic();
		et.learnTrees(5, 200, nTrees);
		Timer.toc("FactorExtraTrees.learnTrees");
		
		// get all predictions by trees:
		Matrix all = et.getAllValues(et.input);
		assertEquals(et.input.nrows(), all.nrows());
		assertEquals(nTrees, all.ncols());
		int[] yhat = et.getValues(et.input);
		// check if their mean is equal to extraTree predictions:
		//System.out.println(all);
		int errors = 0;
		for (int row=0; row<yhat.length; row++) {
			if (yhat[row] != et.output[row]) {
				errors++;
			}
		}
		System.out.println( String.format("Error rate: %1.3f", errors / (double) yhat.length) );
	}

	@Test
	public void testRegression() {
		int ndata = 1000;
		int nTrees = 100;
		int inputDim = 800;
		ExtraTrees et = getSampleDataRegression(ndata, inputDim);
		assertEquals( false, et.isHasNaN() );
		
		Timer.tic();
		et.learnTrees(5, 200, nTrees);
		Timer.toc("ExtraTrees.learnTrees");
		
		// get all predictions by trees:
		Matrix all = et.getAllValues(et.input);
		assertEquals(et.input.nrows(), all.nrows());
		assertEquals(nTrees, all.ncols());
		Timer.tic();
		double[] yhat = et.getValues(et.input);
		Timer.toc("ExtraTrees.getValues()");
		// check if their mean is equal to extraTree predictions:
		//System.out.println(all);
		double error = 0;
		for (int row=0; row<yhat.length; row++) {
			error += Math.pow(yhat[row] - et.output[row], 2);
		}
		System.out.println( String.format("Regression MSE: %1.5f", error / (double) yhat.length) );
	}

}
