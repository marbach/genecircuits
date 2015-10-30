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

import java.util.HashMap;
import java.util.Map.Entry;


/**
 * 
 */
public class Inputs {

	/** The tfs of this node with their their weight across samples (cell types) */
	private HashMap<Node, Double> weights_ = null;
	
	
	// ============================================================================
	// PUBLIC METHODS
	
	/** Constructor */
	public Inputs() {

		weights_ = new HashMap<Node, Double>();
	}

	
	// ----------------------------------------------------------------------------

	/** Add a regulator with it's weight vector (use max if several instances of same tf are added) */
	public void add(Node node, double wNew) {

		Double wPrev = weights_.get(node);
		if (wPrev == null || wNew > wPrev)
			weights_.put(node, wNew);
	}

	
	// ----------------------------------------------------------------------------

	/** Add given tf inputs (use max for each tf) */
	public void add(Inputs other) {

		for (Entry<Node, Double> entry : other.weights_.entrySet())
			add(entry.getKey(), entry.getValue());
	}


	// ----------------------------------------------------------------------------

	/** Multiply TF inputs by a constant */
	public void multiply(double x) {

		for (Entry<Node, Double> entry : weights_.entrySet())
			weights_.put(entry.getKey(), x*entry.getValue());
	}

	
	// ============================================================================
	// PRIVATE METHODS
		

	// ============================================================================
	// GETTERS AND SETTERS

	public HashMap<Node, Double> getWeights() { return weights_; }
	
}
