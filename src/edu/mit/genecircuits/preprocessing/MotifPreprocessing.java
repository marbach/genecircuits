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
package edu.mit.genecircuits.preprocessing;


import edu.mit.genecircuits.FileExport;
import edu.mit.genecircuits.FileParser;


/**
 * Preprocess Pouya's motifs
 */
public class MotifPreprocessing {

	/** Directory with original motif instance files */
	private static String motifDirectory_ = "data/motifs/tfh/";
	
//	/** Motif ids */
//	private HashSet<String> motifs_ = null;
//	/** Motif instances */
//	private HashMap<MotifInstance, Integer> instances_ = null;
//	
//	/** Control motif ids */
//	private HashSet<String> controlMotifs_ = null;
//	/** Motif instance */
//	private HashMap<MotifInstance, Integer> controlInstances_ = null;
	
	
	// ============================================================================
	// PUBLIC METHODS
	
	/** Convert individual confidence score files to bed */
	public static void convertToBedIndividual() {
						
		for (int i=9; i>=9; i--) {
			// Open the input file
			String confidence = "0." + i;
			String filename = motifDirectory_ + confidence + ".gz";
			FileParser parser = new FileParser(filename);
			parser.setSeparator(" ");
			
			// Open the output files for the motifs and the control motifs
			String tmpMotifFile = motifDirectory_ + "tmp_tfh_motifs_0." + i + ".bed";
			String tmpControlFile = motifDirectory_ + "tmp_tfh_controlMotifs_0." + i + ".bed";
			FileExport motifOut = new FileExport(tmpMotifFile, true);
			FileExport controlOut = new FileExport(tmpControlFile, true);
			
			while (true) {
				// Read line
				String[] nextLine = parser.readLine();
				if (nextLine == null)
					break;
				
				// Check number of columns cv
				assert nextLine.length == 7;
				
				// Parse id
				String[] motifIdParsed = nextLine[0].split("_");
				// Check format
				assert motifIdParsed.length == 3 || motifIdParsed.length == 4;
				assert motifIdParsed[2].equals("8mer");

				String id = motifIdParsed[0] + "_" + motifIdParsed[1];
				//String chr = nextLine[1];
				int start = Integer.parseInt(nextLine[2]) - 1; // 0-based
				//int end = Integer.parseInt(nextLine[3]);
				//assert nextLine[4].equals("+") || !nextLine[4].equals("-");
				//boolean posStrand = nextLine[4].equals("+");
				
				// Check if it is a control motif
				boolean isControlMotif = motifIdParsed.length == 4;
				if (isControlMotif) {
					// Add control number to id
					assert motifIdParsed[3].startsWith("C");
					id += "_" + motifIdParsed[3];
				}

				String bed = nextLine[1] + "\t" + start + "\t" + nextLine[3] + "\t" + id + "\t" + confidence + "\t" + nextLine[4] + "\n";
				
				if (isControlMotif)
					controlOut.print(bed);
				else
					motifOut.print(bed);
			} 
			parser.close();
			controlOut.close();
			motifOut.close();
			
//			// Sort the files
//			String motifFile = motifDirectory_ + "tfh_motifs_0." + i + ".bed";
//			String controlFile = motifDirectory_ + "tfh_controlMotifs_0." + i + ".bed";
//
//			NgseaUtils.exec("ls -l");
//			//NgseaUtils.exec("sort-bed --max-mem 4G" + tmpMotifFile + ">" + motifFile);
//			//NgseaUtils.exec("sort-bed --max-mem 4G" + tmpControlFile + ">" + controlFile);
//
//			// Delete tmp files
//			new File(tmpMotifFile).delete();
//			new File(tmpControlFile).delete();
		}
	}

	
	// ----------------------------------------------------------------------------

	/** Write a file that gives for each motif instance the max confidence score */
	public static void convertToBed() {
				
		// Open the output files for the motifs and the control motifs
		String motifFile = motifDirectory_ + "motifsAll.bed";
		String controlFile = motifDirectory_ + "controlMotifsAll.bed";
		FileExport motifOut = new FileExport(motifFile, true);
		FileExport controlOut = new FileExport(controlFile, true);

		for (int i=9; i>=0; i--) {
			// Open the input file
			String confidence = "0." + i;
			String filename = motifDirectory_ + confidence + ".gz";
			FileParser parser = new FileParser(filename);
			parser.setSeparator(" ");
			
//			// Open the output files for the motifs and the control motifs
//			String motifFile = motifDirectory_ + "motifs_0." + i + ".bed";
//			String controlFile = motifDirectory_ + "controlMotifs_0." + i + ".bed";
//			FileExport motifOut = new FileExport(motifFile, true);
//			FileExport controlOut = new FileExport(controlFile, true);
			
			while (true) {
				// Read line
				String[] nextLine = parser.readLine();
				if (nextLine == null)
					break;
				
				// Check number of columns
				assert nextLine.length == 7;
				
				// Parse id
				String[] motifIdParsed = nextLine[0].split("_");
				// Check format
				assert motifIdParsed.length == 3 || motifIdParsed.length == 4;
				assert motifIdParsed[2].equals("8mer");

				String id = motifIdParsed[0] + "_" + motifIdParsed[1];
				//String chr = nextLine[1];
				int start = Integer.parseInt(nextLine[2]) - 1; // 0-based
				//int end = Integer.parseInt(nextLine[3]);
				//assert nextLine[4].equals("+") || !nextLine[4].equals("-");
				//boolean posStrand = nextLine[4].equals("+");
				
				// Check if it is a control motif
				boolean isControlMotif = motifIdParsed.length == 4;
				if (isControlMotif) {
					// Add control number to id
					assert motifIdParsed[3].startsWith("C");
					id += "_" + motifIdParsed[3];
				}

				String bed = nextLine[1] + "\t" + start + "\t" + nextLine[3] + "\t" + id + "\t" + confidence + "\t" + nextLine[4] + "\n";
				
				if (isControlMotif)
					controlOut.print(bed);
				else
					motifOut.print(bed);
			} 
			parser.close();
		}
		controlOut.close();
		motifOut.close();
	}

	
//	/** Write a file that gives for each motif instance the max confidence score */
//	public void collapseMotifFiles() {
//				
//		// Initialize
//		instances_ = new HashMap<String, Integer>();
//		
//		// Read the files, record for each motif instance the max value of i where it was found
//		for (int i=6; i<=9; i++) {
//			// Open the file
//			String filename = motifDirectory_ + "0." + i + ".gz";
//			FileParser parser = new FileParser(filename);
//			parser.setSeparator(" ");
//			
//			while (true) {
//				// Read line
//				String[] nextLine = parser.readLine();
//				if (nextLine == null)
//					break;
//				
//				// Check number of columns
//				if (nextLine.length != 7)
//					throw new RuntimeException("Expected 7 columns in tfh motif files");
//				
//				String motifInstance = nextLine[0] + " " + nextLine[1] + " " + nextLine[2] + " " + nextLine[3] + " " + nextLine[4];
//				instances_.put(motifInstance, i);
//			} 
//			parser.close();
//		}
//	}

		
	// ============================================================================
	// GETTERS AND SETTERS
		
	/*

	
	// Create the motif instance
	MotifInstance instance = new MotifInstance(id, chr, start, end, posStrand);
	// Add it
	if (isControlMotif) {
		assert firstIteration || (controlMotifs_.contains(id));
		assert firstIteration || (controlInstances_.containsKey(instance)); 
		controlMotifs_.add(id);
		controlInstances_.put(instance, i);
	} else {
		assert firstIteration || (motifs_.contains(id) && instances_.containsKey(instance)); 
		motifs_.add(id);
		instances_.put(instance, i);
	}
	*/

	
}
