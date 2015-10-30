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
package edu.mit.genecircuits;

import java.io.File;

import edu.mit.genecircuits.net.CircuitBuilder;
import edu.mit.genecircuits.preprocessing.MotifPreprocessing;
import edu.mit.genecircuits.preprocessing.OntologyPreprocessing;


/**
 * Main class Gene Module Weaver (GMW) MAGNuM (Multi-scale Analysis of Gene
 * Network Modules) Multi-scale analysis of GWAS Adam (Adaptive DiseAse Modules)
 */
@SuppressWarnings("unused")
public class GcMain {

	// ============================================================================
	// STATIC METHODS

	/** Main function */
	static public void main(String[] args) {

		try {
			GcMain gc = new GcMain(args);
			gc.run();
		} catch (Exception e) {
			error(e);
		}
	}

	// ----------------------------------------------------------------------------

	/** Print the stack trace of the exception and exit */
	static public void error(Exception e) {
		e.printStackTrace();
		System.exit(-1); // return -1 in case of error
	}

	/** Print the stack trace of the exception and exit with error message */
	static public void error(Exception e, String msg) {
		System.err.println("ERROR: " + msg);
		error(e);
	}

	/** Print error message and exit */
	static public void error(String msg) {
		System.err.println("ERROR: " + msg);
		System.exit(-1);
	}

	/** Print warning message to stderr */
	static public void warning(String msg) {
		System.out.println("WARNING: " + msg);
		System.out.flush();
	}

	/** Write line to stdout */
	static public void println(String msg) {
		System.out.println(msg);
	}

	/** Write empty line to stdout */
	static public void println() {
		System.out.println();
	}

	/** Write string to stdout */
	static public void print(String msg) {
		System.out.print(msg);
	}

	// ============================================================================
	// PUBLIC METHODS

	/** Constructor, parse command-line arguments, initialize settings */
	public GcMain(String[] args) {

		GcMain.println("SETTINGS FILE");
		GcMain.println("-------------\n");

		// Parse command-line arguments and initialize settings
		GcOptionParser optionParser = new GcOptionParser();
		optionParser.parse(args);

		// Create output directory
		File outputDir = new File(Settings.outputDirectory_);
		if (!outputDir.exists())
			outputDir.mkdirs();
	}

	// ----------------------------------------------------------------------------

	/**
	 * Parse the command-line arguments, read the files, perform network
	 * inference, write outputs
	 */
	public void run() {

		if (Settings.mode_ == 1)
			runPreprocessing();
		else if (Settings.mode_ == 2)
			runCircuitBuilder();
		else
			throw new IllegalArgumentException("Property 'mode' must be either '1' or '2'");

		System.out.println("Success!");
	}


	// ----------------------------------------------------------------------------

	/** Preprocess data */
	public void runPreprocessing() {

		// Pouya's motifs
		// MotifPreprocessing.convertToBed();
		//MotifPreprocessing.convertToBedIndividual();
		
		// FANTOM ontology
		//new OntologyPreprocessing().writeIdNameMap();
		new OntologyPreprocessing().writeIdRelationships();
		
	}

	
	// ----------------------------------------------------------------------------

	/** Build cell-type specific circuits from intermediary files */
	public void runCircuitBuilder() {

		CircuitBuilder circuitBuilder = new CircuitBuilder();
		circuitBuilder.run();
	}

	
	// ============================================================================
	// PRIVATE METHODS


}
