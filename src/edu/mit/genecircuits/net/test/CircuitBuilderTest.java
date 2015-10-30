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
package edu.mit.genecircuits.net.test;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.*;

import edu.mit.genecircuits.Settings;
import edu.mit.genecircuits.net.*;


/**
 * Unit tests for NetworkAnalyzerBasicProperties
 */
public class CircuitBuilderTest extends CircuitBuilder {
	
	
	// ============================================================================
	// SETUP
	
	@BeforeClass
	public static void testSetup() {

		Settings.loadSettings();
		Settings.suffix_ = "";
		Settings.circuitDir_ = "src/edu/mit/genecircuits/net/test";
		Settings.circuitOutDir_ = "tmp";		
		
		Settings.tfPromoterFile_ =  "tf---promoter";
		Settings.promoterTranscriptFile_ = "promoter---transcript";
		Settings.promoterExprFile_ = "promoter_expr";
		
		Settings.tfEnhancerFile_ = "tf---enhancer";
		Settings.enhancerTranscriptFile_ = "enhancer---transcript";
		Settings.enhancerExprFile_ = "enhancer_expr";
		Settings.transcriptExprFile_ = "transcript_expr";
	}

	
	@AfterClass
	public static void testCleanup() {
	}
	
	
	// ============================================================================
	// TESTS

	/** tf---promoter---gene */
	@Test
	public void buildTfPromoterGeneTest() {
		
		double delta = 1e-6;

		// Load promoter expression
		loadPromoterExpr();
		
		// ========
		// Sample 1
		
		buildTfPromoterGene(0);
		Circuit circuit = getCircuit();
		
		// The gene
		HashMap<String, Gene> genes = circuit.getGenes();
		assertEquals(1, genes.size());
		Gene g1 = genes.get("g1");

		// The TFs
		HashMap<String, Tf> tfs = circuit.getTfs();
		assertEquals(3, tfs.size());
		Tf tf2 = tfs.get("tf2");
		Tf tf3 = tfs.get("tf3");
		Tf tf4 = tfs.get("tf4");
		
		// TF inputs
		HashMap<Node, Double> tfIn = g1.getTfInputs().getWeights();
		assertEquals(3, tfIn.size());
		assertEquals(0.2, tfIn.get(tf3), delta);
		assertEquals(0.0444, tfIn.get(tf2), delta);
		assertEquals(0.21, tfIn.get(tf4), delta);

		// ========
		// Sample 2
		
		buildTfPromoterGene(1);
		circuit = getCircuit();
		
		// The genes
		genes = circuit.getGenes();
		assertEquals(2, genes.size());
		g1 = genes.get("g1");
		Gene g2 = genes.get("g2");

		// The TFs
		tfs = circuit.getTfs();
		assertEquals(3, tfs.size());
		tf2 = tfs.get("tf2");
		tf3 = tfs.get("tf3");
		tf4 = tfs.get("tf4");
		
		// TF inputs g1
		tfIn = g1.getTfInputs().getWeights();
		assertEquals(2, tfIn.size());
		assertEquals(0.0888, tfIn.get(tf2), delta);
		assertEquals(0.2, tfIn.get(tf3), delta);
		
		// TF inputs g2
		tfIn = g2.getTfInputs().getWeights();
		assertEquals(1, tfIn.size());
		assertEquals(0.4, tfIn.get(tf4), delta);
		
		// ========
		// Sample 3
		
		buildTfPromoterGene(2);
		circuit = getCircuit();
		
		// The genes
		genes = circuit.getGenes();
		assertEquals(2, genes.size());
		g1 = genes.get("g1");
		g2 = genes.get("g2");

		// The TFs
		tfs = circuit.getTfs();
		assertEquals(2, tfs.size());
		tf3 = tfs.get("tf3");
		tf4 = tfs.get("tf4");
		
		// TF inputs g1
		tfIn = g1.getTfInputs().getWeights();
		assertEquals(2, tfIn.size());
		assertEquals(0.06, tfIn.get(tf3), delta);
		assertEquals(0.07, tfIn.get(tf4), delta);
		
		// TF inputs g2
		tfIn = g2.getTfInputs().getWeights();
		assertEquals(1, tfIn.size());
		assertEquals(0.48, tfIn.get(tf4), delta);
	}

	
	/** tf---enhancer---gene */
	@Test
	public void buildTfEnhancerGeneTest() {
		
		double delta = 1e-6;

		// Load enhancer and transcript expression
		loadEnhancerExpr();
		loadTranscriptExpr();
		
		// ========
		// Sample 1
		
		buildTfEnhancerGene(0);
		Circuit circuit = getCircuit();
		
		// The gene
		HashMap<String, Gene> genes = circuit.getGenes();
		assertEquals(1, genes.size());
		Gene g1 = genes.get("g1");

		// The TFs
		HashMap<String, Tf> tfs = circuit.getTfs();
		assertEquals(1, tfs.size());
		Tf tf2 = tfs.get("tf2");
		
		// TF inputs
		HashMap<Node, Double> tfIn = g1.getTfInputs().getWeights();
		assertEquals(1, tfIn.size());
		assertEquals(0.08622204, tfIn.get(tf2), delta);

		// ========
		// Sample 2
		
		buildTfEnhancerGene(1);
		circuit = getCircuit();
		
		// The genes
		genes = circuit.getGenes();
		assertEquals(1, genes.size());
		g1 = genes.get("g1");
		//Gene g2 = genes.get("g2");

		// The TFs
		tfs = circuit.getTfs();
		assertEquals(2, tfs.size());
		Tf tf1 = tfs.get("tf1");
		tf2 = tfs.get("tf2");
		
		// TF inputs g1
		tfIn = g1.getTfInputs().getWeights();
		assertEquals(2, tfIn.size());
		assertEquals(0.02683282, tfIn.get(tf1), delta);
		assertEquals(0.05366563, tfIn.get(tf2), delta);
		
		// ========
		// Sample 3

		buildTfEnhancerGene(2);
		circuit = getCircuit();
		
		// The genes
		genes = circuit.getGenes();
		assertEquals(2, genes.size());
		g1 = genes.get("g1");
		Gene g2 = genes.get("g2");

		// The TFs
		tfs = circuit.getTfs();
		assertEquals(2, tfs.size());
		tf1 = tfs.get("tf1");
		tf2 = tfs.get("tf2");
		
		// TF inputs g1
		tfIn = g1.getTfInputs().getWeights();
		assertEquals(2, tfIn.size());
		assertEquals(0.01131371, tfIn.get(tf1), delta);
		assertEquals(0.02910326, tfIn.get(tf2), delta);

		// TF inputs g2
		tfIn = g2.getTfInputs().getWeights();
		assertEquals(1, tfIn.size());
		assertEquals(0.01425763, tfIn.get(tf2), delta);
}

	


	// ============================================================================
	// PRIVATE METHODS


}
