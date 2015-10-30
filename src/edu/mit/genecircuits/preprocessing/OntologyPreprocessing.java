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


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.mit.genecircuits.FileExport;
import edu.mit.genecircuits.FileParser;


/**
 * Preprocess FANTOM ontology
 */
public class OntologyPreprocessing {

	/** Directory with original motif instance files */
	private String ontologyDirectory_ = null;
	/** The obo file defining the ontology */
	private String oboFile_ = null;
	
	/** The input file parser */
	private FileParser parser_ = null;
	
	
	// ============================================================================
	// PUBLIC METHODS
	
	/** Constructor */
	public OntologyPreprocessing() {
		
		ontologyDirectory_ = "data/fantom5/ontology/";
		oboFile_ = "ff-phase1-prerelease-20130717.obo";
	}
	
	// ----------------------------------------------------------------------------

	
	/** Generate an ID-name table from the obo file */
	public void writeIdNameMap() {
						
		// The map
		HashMap<String, String> id_name = loadIdNameMap();
				
		// Convert first to an array list so we can sort it
		ArrayList<String> output = new ArrayList<String>();
		for (String id : id_name.keySet())
			output.add(id + "\t" + id_name.get(id));
		Collections.sort(output);
		
		// Write table
		String filename = ontologyDirectory_ + "id2name.txt";
		FileExport writer = new FileExport(filename);
		for (String line : output)
			writer.println(line);
		writer.close();
	}
	
	
	// ----------------------------------------------------------------------------

	/** Generate child-parent ID map */
	public void writeIdRelationships() {

		// The map
		HashMap<String, String> id_name = loadIdNameMap();

		// Open the input file
		String filename = ontologyDirectory_ + oboFile_;
		String outfile = ontologyDirectory_ + "childParentIds.txt";

		// Input
		parser_ = new FileParser(filename);
		parser_.setSeparator(": ");
		// Output
		FileExport writer = new FileExport(outfile);
		
		while (true) {
			// Read line
			String[] nextLine = parser_.readLine();
			if (nextLine == null)
				break;
				
			if (nextLine[0].equalsIgnoreCase("[Term]")) {
				// Parse the ID
				String id = parseId();
				// Skip the name and the namespace
				if (!parser_.readLine()[0].equalsIgnoreCase("name"))
					parser_.error("Expected 'name'");
				if (!parser_.readLine()[0].equalsIgnoreCase("namespace"))
					parser_.error("Expected 'namespace'");

				// Parse the relationships
				while(true) {
					nextLine = parser_.readLine();
					if (nextLine.length == 1 && nextLine[0].isEmpty())
						break;

					String[] parentStr = nextLine[1].split(" ");
					String parentId = null;
					if (id_name.containsKey(parentStr[0])) {
						if (nextLine[0].equalsIgnoreCase("is_a") ||
								nextLine[0].equalsIgnoreCase("intersection_of"))
							parentId = parentStr[0];
						else if (!nextLine[0].equalsIgnoreCase("disjoint_from") &&
								!nextLine[0].equalsIgnoreCase("union_of"))
							parser_.error("Unknown relationship: " + nextLine[0]);
						
					} else if (parentStr.length > 1 && id_name.containsKey(parentStr[1])) {
						if (!nextLine[0].equalsIgnoreCase("relationship") &&
								!nextLine[0].equalsIgnoreCase("intersection_of"))
							parser_.error("Unknown def: " + nextLine[0]);
						if (parentStr[0].equalsIgnoreCase("part_of") ||
								parentStr[0].equalsIgnoreCase("develops_from") ||
								parentStr[0].equalsIgnoreCase("derives_from") ||
								parentStr[0].equalsIgnoreCase("is_model_for"))
							parentId = parentStr[1];
						else if (!parentStr[0].equalsIgnoreCase("union_of"))
							parser_.error("Unknown relationship: " + parentStr[0]);
					}
						
					// Write the pair
					if (parentId != null)
						writer.println(id + "\t" + parentId);
				}
			}
		} 
		parser_.close();
		writer.close();

	}

	
	// ============================================================================
	// PRIVATE METHODS
	
	/** Load id-name map */
	private HashMap<String, String> loadIdNameMap() {
		
		// The map
		HashMap<String, String> id_name = new HashMap<String, String>();
		
		// Open the input file
		//String filename = ontologyDirectory_ + oboFile_;
		parser_ = new FileParser("/Users/marbach/Java/genecircuits/results/gene_circuits/fantom5/fantom5_net/tfGeneNet/tf---gene.prec90/level2.scaled10.c32/level2.scaled10.c32.txt");
		parser_.setSeparator(": ");
			
		while (true) {
			// Read line
			String[] nextLine = parser_.readLine();
			if (nextLine == null)
				break;
				
			if (nextLine[0].equalsIgnoreCase("[Term]")) {
				// Parse ID
				String id = parseId();
				
				// Parse the name
				nextLine = parser_.readLine();
				// Check format
				if (!(nextLine.length == 2) && !nextLine[0].equalsIgnoreCase("name"))
					parser_.error("Expected 'name: ...' after 'id: ...'");
				// The name
				String name = nextLine[1];

				// Check that IDs are unique
				if (id_name.containsKey(id))
					parser_.error("ID is not unique: " + id);
				// Add the pair
				id_name.put(id, name);
			}
		} 
		parser_.close();
		return id_name;
	}

	
	// ----------------------------------------------------------------------------

	/** Parse ID */
	private String parseId() {
		
		// Read line
		String[] nextLine = parser_.readLine();
		// Check format
		if (!(nextLine.length == 2) && !nextLine[0].equalsIgnoreCase("id"))
			parser_.error("Expected 'id: ...' after [Term]");
		// The id
		return(nextLine[1]);
	}
	
}
