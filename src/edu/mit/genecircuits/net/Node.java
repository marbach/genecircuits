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



/**
 * 
 */
public class Node {

	/** Name */
	protected String id_ = null;
	/** The tfs of this element with their motif confidence score */
	protected Inputs tfInputs_ = null;
	/** Expression value */
	protected double expr_ = -1;
	
	
	// ============================================================================
	// PUBLIC METHODS
	
	/** Constructor */
	public Node(String id) {
		
		id_ = id;
	}
	

	// ----------------------------------------------------------------------------

	/** Add a tf */
	public void add(Tf tf, double w) {

		tfInputs_.add(tf, w);
	}

	
//	// ----------------------------------------------------------------------------
//
//	/** Add the given tf inputs (take max for tfs that are already present) */
//	public void add(TfInputs tfInputs) {
//
//		if (tfInputs_ == null)
//			tfInputs_ = new TfInputs();
//		
//		tfInputs_.add(tfInputs);
//	}

	
	
	// ============================================================================
	// PRIVATE METHODS
		

	// ============================================================================
	// GETTERS AND SETTERS

	public String getId() { return id_; }
	
	public Inputs getTfInputs() { return tfInputs_; }
	public void setTfInputs(Inputs tfs) { tfInputs_ = tfs; }
	
	public double getExpr() { return expr_; }
	public void setExpr(double x) { expr_ = x; }
	
}
