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

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import edu.mit.genecircuits.FileParser;
import edu.mit.genecircuits.GcMain;
import edu.mit.genecircuits.Settings;


/**
 * 
 */
public class CircuitBuilder {

	/** The samples (cell types) */
	private String[] samples_ = null;
	/** Promoter/enhancer expression matrix */
	private ExpressionMatrix elementExpr_ = null;
	/** Transcript expression matrix */
	private ExpressionMatrix transcriptExpr_ = null;

	/** The regulatory circuit that is being built */
	private Circuit circuit_ = null;
	
	
	
	// ============================================================================
	// PUBLIC METHODS
	
	/** Constructor */
	public CircuitBuilder() {

	}

	
	// ----------------------------------------------------------------------------

	/** Main method, choose actions based on settings */
	public void run() {
		
		buildSpecificCircuits();
	}
	
	
	// ============================================================================
	// PRIVATE METHODS
		
	/** Build cell type specific regulatory circuits */
	private void buildSpecificCircuits() {
		
		// tf---promoter---gene.spec
		//buildTfPromoterGene();
		// tf---enhancer---gene
		buildTfEnhancerGene();
	}
	
	
	// ----------------------------------------------------------------------------

	/** tf---promoter---gene */
	protected void buildTfPromoterGene() {
		
		// Load promoter expr
		loadPromoterExpr();		
		
		// Create output directory
		String outDir = Settings.circuitOutDir_ + "/tf---promoter---gene" + Settings.suffix_;
		new File(outDir).mkdirs();

		GcMain.println("\nWiring circuits for " + samples_.length + " samples...");
		
		for (int i=0; i<samples_.length; i++) {
			// Build circuit
			buildTfPromoterGene(i);
			
			// Write TF--gene
			String filename = outDir + "/tf---promoter---gene" + Settings.suffix_ + "." + samples_[i] + ".txt";
			//circuit_.writeTfGene(filename);

			// Write TF--promoter
			filename = outDir + "/tf---promoter" + Settings.suffix_ + "." + samples_[i] + ".txt";
			circuit_.writeTfElement(filename);
		}
	}

	
	// ----------------------------------------------------------------------------

	/** tf---promoter---gene */
	protected void buildTfPromoterGene(int i) {

		// Create circuit
		circuit_ = new Circuit();
		// promoter_expr: add only elements with expression > 0
		addActiveElements(i);
		// promoter---transcript (only for active promoters)
		loadPromoterTranscript(Settings.circuitDir_ + "/" + Settings.promoterTranscriptFile_ + Settings.suffix_ + ".txt");
		// tf---promoter (only for active promoters)
		loadTfElement(Settings.circuitDir_ + "/" + Settings.tfPromoterFile_ + Settings.suffix_ + ".txt");
		
		//GcMain.println("\nLoaded:");
		//circuit_.printInfo();
		
		// Weight tf---promoter by promoter expression
		for (RegElement element : circuit_.getElements().values())
			element.getTfInputs().multiply(element.getExpr());

		for (Gene g : circuit_.getGenes().values()) {
			Inputs tfInputs = new Inputs();
		
			// For each transcript of this gene
			for (Transcript t : g.getTranscripts())
				for (Node p : t.getElementInputs().getWeights().keySet())
					tfInputs.add(p.getTfInputs());

			g.setTfInputs(tfInputs);
		}
	}
	
		
	// ----------------------------------------------------------------------------

	/** Load the expression matrix */
	protected void loadPromoterExpr() {

		String filename = Settings.circuitDir_ + "/" + Settings.promoterExprFile_ + Settings.suffix_ + ".txt.gz";
		elementExpr_ = new ExpressionMatrix(filename);
		samples_ = elementExpr_.getSamples();
	}
	
	
	// ----------------------------------------------------------------------------

	/** tf---enhancer---gene */
	protected void buildTfEnhancerGene() {
		
		// Load enhancer and transcript expr
		loadEnhancerExpr();		
		loadTranscriptExpr();
		
		// Create output directory
		String outDir = Settings.circuitOutDir_ + "/tf---enhancer---gene" + Settings.suffix_;
		new File(outDir).mkdirs();

		GcMain.println("\nWiring circuits for " + samples_.length + " samples...");
		
		for (int i=0; i<samples_.length; i++) {
			// Build circuit
			buildTfEnhancerGene(i);

			// Write TF--gene
			String filename = outDir + "/tf---enhancer---gene" + Settings.suffix_ + "." + samples_[i] + ".txt";
			//circuit_.writeTfGene(filename);
			
			// Write TF--enhancer
			filename = outDir + "/tf---enhancer" + Settings.suffix_ + "." + samples_[i] + ".txt";
			//circuit_.writeTfElement(filename);
			
			// Write enhancer---transcript
			filename = outDir + "/enhancer---gene" + Settings.suffix_ + "." + samples_[i] + ".txt";
			circuit_.writeEnhancerGene(filename);
		}
	}

	
	// ----------------------------------------------------------------------------

	/** tf---enhancer---gene */
	protected void buildTfEnhancerGene(int i) {

		// Create circuit
		circuit_ = new Circuit();
		// enhancer_expr: add only elements with expression > 0
		addActiveElements(i);
		// transcrpt_expr: add only transcripts with expression > 0
		addActiveTranscripts(i);
		// enhancer---transcript (only for active enhancers)
		loadEnhancerTranscript(Settings.circuitDir_ + "/" + Settings.enhancerTranscriptFile_ + Settings.suffix_ + ".txt");
		// tf---enhancer (only for active enhancers)
		loadTfElement(Settings.circuitDir_ + "/" + Settings.tfEnhancerFile_ + Settings.suffix_ + ".txt");
		
		//GcMain.println("\nLoaded:");
		//circuit_.printInfo();
		
		for (Gene g : circuit_.getGenes().values()) {
			Inputs tfInputs = new Inputs();
			Inputs enhancerInputs = new Inputs();
		
			// For each transcript of this gene
			for (Transcript t : g.getTranscripts()) {
				// enhancer---transcript_t
				HashMap<Node, Double> enhancerInputs_t = t.getElementInputs().getWeights();
				// transcript expression
				double transcriptExpr = t.getExpr();
				
				for (Node e : enhancerInputs_t.keySet()) {
					double distanceWeight = enhancerInputs_t.get(e);
					double exprWeight = Math.sqrt(e.getExpr() * transcriptExpr);
					double enhancerTranscriptWeight = distanceWeight * exprWeight;
					// DANGEROUS, check that this doesn't affect the iterator (we are looping over this)
					//enhancerInputs_t.put(e, enhancerTranscriptWeight);
					enhancerInputs.add(e, enhancerTranscriptWeight);
					
					// For each tf of this enhancer, add it to the tf inputs of the transcript
					for (Entry<Node, Double> entry : e.getTfInputs().getWeights().entrySet()) {
						double x = entry.getValue() * enhancerTranscriptWeight;
						tfInputs.add(entry.getKey(), x);
					}
				}
			}
			g.setTfInputs(tfInputs);
			g.setEnhancerInputs(enhancerInputs);
		}
		
		// Weight tf---enhancer by enhancer expression
		for (RegElement element : circuit_.getElements().values())
			element.getTfInputs().multiply(element.getExpr());
	}

	
	// ----------------------------------------------------------------------------

	/** Load the expression matrix */
	protected void loadEnhancerExpr() {

		String filename = Settings.circuitDir_ + "/" + Settings.enhancerExprFile_ + ".txt.gz";
		elementExpr_ = new ExpressionMatrix(filename);
		setSamples(elementExpr_.getSamples());
	}

	
	// ----------------------------------------------------------------------------

	/** Load the expression matrix */
	protected void loadTranscriptExpr() {

		String filename = Settings.circuitDir_ + "/" + Settings.transcriptExprFile_ + Settings.suffix_ + ".txt.gz";
		transcriptExpr_ = new ExpressionMatrix(filename);
		setSamples(transcriptExpr_.getSamples());
	}

	
	// ----------------------------------------------------------------------------

	/** Set samples, check that they are consistent if they were set before */
	private void setSamples(String[] samples) {

		if (samples_ == null) {
			samples_ = samples;
		} else {
			for (int i=0; i<samples_.length; i++)
				if (!samples_[i].equals(samples[i]))
					throw new RuntimeException("Inconsistent samples");
		}
	}
	
	
	// ----------------------------------------------------------------------------

	/** Add elements with expr > 0 to the circuit */
	private void addActiveElements(int k) {

		String[] nodeIds = elementExpr_.getNodes();
		double[] x = elementExpr_.getX().getColumn(k);
		int N = nodeIds.length;
		if (N != x.length)
			throw new RuntimeException("Inconsistent length");
		
		for (int i=0; i<N; i++) {
			if (x[i] != 0) {
				Node node = circuit_.addElement(nodeIds[i]);
				node.setExpr(x[i]);
			}
		}
	}
	
	
	/** Add transcripts with expr > 0 to the circuit */
	private void addActiveTranscripts(int k) {

		String[] nodeIds = transcriptExpr_.getNodes();
		double[] x = transcriptExpr_.getX().getColumn(k);
		int N = nodeIds.length;
		if (N != x.length)
			throw new RuntimeException("Inconsistent length");
		
		for (int i=0; i<N; i++) {
			if (x[i] != 0) {
				Node node = circuit_.addTranscript(nodeIds[i]);
				node.setExpr(x[i]);
			}
		}
	}

	
	// ----------------------------------------------------------------------------

	/** promoter---transcript---gene (the elements and their expression have to be preloaded) */
	private void loadPromoterTranscript(String filename) {

		// Open the file
		FileParser parser = new FileParser(filename, true);
		// Skip header
		parser.skipLines(1);
		
		while (true) {
			String[] nextLine = parser.readLine();
			if (nextLine == null)
				break;
			
			String elementId = nextLine[0];
			String transcriptId = nextLine[1];
			String geneId = nextLine[3];
			
			// Add the links and nodes if this element is active
			RegElement element = circuit_.getElement(elementId);
			if (element != null) {
				// Create / get the nodes
				Transcript transcript = circuit_.addTranscript(transcriptId);
				Gene gene = circuit_.addGene(geneId);
				// Add the links
				gene.add(transcript);
				transcript.add(element, 1);
			}
		}
		parser.close();
	}


	/** enhancer---transcript---gene (the elements and their expression have to be preloaded) */
	private void loadEnhancerTranscript(String filename) {

		// Open the file
		FileParser parser = new FileParser(filename, true);
		// Skip header
		parser.skipLines(1);
		
		while (true) {
			String[] nextLine = parser.readLine();
			if (nextLine == null)
				break;
		
			String elementId = nextLine[0];
			String transcriptId = nextLine[1];
			String weightStr = nextLine[3];
			String geneId = nextLine[4];

			// Add the links and nodes if they don't exist yet
			RegElement element = circuit_.getElement(elementId);
			Transcript transcript = circuit_.getTranscript(transcriptId);
			if (element != null && transcript != null) {
				// Create / get the nodes
				Gene gene = circuit_.addGene(geneId);
				// Add the links
				gene.add(transcript);
				transcript.add(element, Double.parseDouble(weightStr));
			}
		}
		parser.close();
	}

	
	// ----------------------------------------------------------------------------

	/** tf---promoter---gene */
	private void loadTfElement(String filename) {

		// Open the file
		FileParser parser = new FileParser(filename, true);
		// Skip header
		parser.skipLines(1);
		
		while (true) {
			String[] nextLine = parser.readLine();
			if (nextLine == null)
				break;

			RegElement element = circuit_.getElement(nextLine[1]); 
			if (element != null) {
				// Add the tf---element link and nodes if they dont' exist yet
				double motifConf = Double.parseDouble(nextLine[2]) + 0.1;
				if (motifConf < 0.1 || motifConf > 1.0)
					throw new IllegalArgumentException("Motif score must be in (0.1, 1.0)");

				circuit_.addTfElement(nextLine[0], element, motifConf);
			}
		}
		parser.close();
	}


	// ============================================================================
	// GETTERS AND SETTERS

	public Circuit getCircuit() { return circuit_; }
	
}
