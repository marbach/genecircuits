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

import edu.mit.genecircuits.FileExport;
import edu.mit.genecircuits.GcMain;
import edu.mit.genecircuits.GcUtils;


/**
 * 
 */
public class Circuit {

	/** The TFs */
	private HashMap<String, Tf> tfs_ = null;
	/** The regulatory elements */
	private HashMap<String, RegElement> elements_ = null;
	/** The transcripts */
	private HashMap<String, Transcript> transcripts_ = null;
	/** The genes */
	private HashMap<String, Gene> genes_ = null;
		
	
	// ============================================================================
	// PUBLIC METHODS
	
	/** Constructor */
	public Circuit() {

		tfs_ = new HashMap<String, Tf>();
		elements_ = new HashMap<String, RegElement>();
		transcripts_ = new HashMap<String, Transcript>();
		genes_ = new HashMap<String, Gene>();
	}

	
	// ----------------------------------------------------------------------------

	/** Print number of genes, transcripts, etc. */
	public void printInfo() {
	
		GcMain.println("- " + tfs_.size() + "\tTFs");
		GcMain.println("- " + elements_.size() + "\tRegulatory elements");
		GcMain.println("- " + transcripts_.size() + "\tTranscripts");
		GcMain.println("- " + genes_.size() + "\tGenes\n");
	}
	
	
	// ----------------------------------------------------------------------------

	/** Write the TF--gene network */
	public void writeTfGene(String filename) {

		writeTfNode(filename, genes_);
	}

	// ----------------------------------------------------------------------------

	/** Write the TF--regElement network */
	public void writeTfElement(String filename) {

		writeTfNode(filename, elements_);
	}


	// ----------------------------------------------------------------------------

	/** Write the TF--gene network */
	@SuppressWarnings("rawtypes")
	private void writeTfNode(String filename, HashMap nodes) {

		// Open file
		FileExport writer = new FileExport(filename, true);
			
		for (Object o : nodes.values()) {
			Node n = (Node) o;
			HashMap<Node, Double> weights = n.getTfInputs().getWeights();
			for (Entry<Node, Double> entry : weights.entrySet()) {
				if (entry.getValue() == 0)
					throw new RuntimeException("There should be no zero weights");
				else
					writer.println(entry.getKey().getId() + "\t" + n.getId() + "\t" + GcUtils.toStringScientific10(entry.getValue()));
			}
		}
		writer.close();
	}

	
	// ----------------------------------------------------------------------------

	/** Write the enhancer--gene network */
	public void writeEnhancerGene(String filename) {

		// Open file
		FileExport writer = new FileExport(filename, true);
			
		for (Gene g : genes_.values()) {
			HashMap<Node, Double> weights = g.getEnhancerInputs().getWeights();
			for (Entry<Node, Double> entry : weights.entrySet()) {
				if (entry.getValue() == 0)
					throw new RuntimeException("There should be no zero weights");
				else
					writer.println(entry.getKey().getId() + "\t" + g.getId() + "\t" + GcUtils.toStringScientific10(entry.getValue()));
			}
		}
		writer.close();
	}

	
	// ----------------------------------------------------------------------------
	
	/** Set expression for */
	public void setExpr(HashMap<String, Node> nodes, String[] nodeIds, double[] expr) {
		
		int N = nodeIds.length;
		if (N != expr.length || N != nodes.size())
			throw new RuntimeException("Inconsistent size of arguments");
			
		// Initialize
		for (Node n : nodes.values())
			n.expr_ = -1;

		// Set expr values
		for (int i=0; i<N; i++)
			nodes.get(nodeIds[i]).expr_ = expr[i];
	}

	
	// ============================================================================
	// ADD NODES

	/** Add / get tf */
	public Tf addTf(String id) {
		
		Tf tf = tfs_.get(id);
		if (tf == null) {
			tf = new Tf(id);
			tfs_.put(id, tf);
		}
		return tf;
	}

	/** Add / get regulatory element */
	public RegElement addElement(String id) {
		
		RegElement element = elements_.get(id);
		if (element == null) {
			element = new RegElement(id);
			elements_.put(id, element);
		}
		return element;
	}

	/** Add / get transcript */
	public Transcript addTranscript(String id) {
		
		Transcript transcript = transcripts_.get(id);
		if (transcript == null) {
			transcript = new Transcript(id);
			transcripts_.put(id, transcript);
		}
		return transcript;
	}

	/** Add / get gene */
	public Gene addGene(String id) {

		Gene gene = genes_.get(id);
		if (gene == null) {
			gene = new Gene(id);
			genes_.put(id, gene);
		}
		return gene;
	}

	
	// ============================================================================
	// ADD LINKS
	
	/** tf---element */
	public void addTfElement(String tfId, RegElement element, double motifConf) {

		// Create / get the nodes
		Tf tf = addTf(tfId);
		// Add the link
		element.add(tf, motifConf);
	}

	
	/** Add transcript---gene */
	public void addTranscriptGene(String transcriptId, String geneId) {
		
		Gene gene = genes_.get(geneId);
		Transcript transcript = transcripts_.get(transcriptId);
		gene.add(transcript);
		//Gene genes_.get(gene).addInput(transcript);
	}

	
	// ============================================================================
	// PRIVATE METHODS
		

	// ============================================================================
	// GETTERS AND SETTERS
	
	public RegElement getElement(String id) { return elements_.get(id); }
	public Transcript getTranscript(String id) { return transcripts_.get(id); }
	
	public HashMap<String, Tf> getTfs() { return tfs_; }
	public HashMap<String, RegElement> getElements() { return elements_; }
	public HashMap<String, Transcript> getTranscripts() { return transcripts_; }
	public HashMap<String, Gene> getGenes() { return genes_; }
	
}
