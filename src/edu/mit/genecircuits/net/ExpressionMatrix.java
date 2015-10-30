/*
Copyright (c) 2013 Daniel Marbach

We release this software open source under an MIT license (see below). If this
software was useful for your scientific work, please cite our paper available at:
http://networkinference.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */
package edu.mit.genecircuits.net;


import org.apache.commons.math3.linear.OpenMapRealMatrix;

import edu.mit.genecircuits.FileParser;



/**
 * 
 */
public class ExpressionMatrix {

	/** The samples (cell types, column names) */
	private String[] samples_ = null;
	/** The nodes (row names) */
	private String[] nodes_ = null;
	
	/** Sparse matrix */
	private OpenMapRealMatrix X_ = null;
	
	
	// ============================================================================
	// PUBLIC METHODS
	
	/** Constructor */
	public ExpressionMatrix(String filename) {

		load(filename);
	}
	
	
	
	// ----------------------------------------------------------------------------

	/** Load expression matrix from file */
	public void load(String filename) {

		// Count lines
		int numNodes = FileParser.countLines(filename) - 1;
		nodes_ = new String[numNodes];
		
		// Open the file
		FileParser parser = new FileParser(filename, true);
		// Header
		samples_ = parser.readLine();
		
		// Initialize expression matrix
		X_ = new OpenMapRealMatrix(numNodes, samples_.length);
		
		for (int i=0; ; i++) {
			String[] nextLine = parser.readLine();
			if (nextLine == null)
				break;

			// Check length
			if (nextLine.length != samples_.length + 1)
				throw new RuntimeException("Incorrect number of columns");

			// Parse row
			nodes_[i] = nextLine[0];
			for (int j=0; j<nextLine.length-1; j++)
				X_.setEntry(i, j, Double.parseDouble(nextLine[j+1]));
		}
		parser.close();
	}

	
	// ----------------------------------------------------------------------------

	
	/** Set samples, check consistency with number of columns */
	public void setSamples(String[] samples) {
		
		if (samples.length != X_.getColumnDimension())
			throw new RuntimeException("Inconsistent number of samples");
		
		samples_ = samples;
	}
	
	
	// ============================================================================
	// PRIVATE METHODS
		

	// ============================================================================
	// GETTERS AND SETTERS

	public String[] getSamples() { return samples_; }
	public String[] getNodes() { return nodes_; }
	public OpenMapRealMatrix getX() { return X_; }
	
}
