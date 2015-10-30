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

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Defines and parses the command-line arguments
 */
public class GcOptionParser extends Settings {

	/** The option parser */
	OptionParser parser_ = null;

	// ============================================================================
	// PUBLIC METHODS

	/** Parse the command-line arguments, initialize all settings */
	public GcOptionParser() {

		// Defines the arguments
		defineArgs();
	}

	// ----------------------------------------------------------------------------

	/** Parses the command-line arguments, which were defined by defineArgs() */
	public void parse(String[] args) {

		// (1) Parse the options
		OptionSet options = parser_.parse(args);

		// Display help
		if (options.has("help")) {
			displayHelp();
			System.exit(0);
		}

		// (2) Set the settings file
		if (options.has("set"))
			settingsFile_ = (String) options.valueOf("set");

		// (3) Load the settings file
		loadSettings();

		// (4) Set command-line options (default is what has been loaded from
		// settings file)
		if (options.has("mode"))
			mode_ = (Integer) options.valueOf("mode");

		if (options.has("outdir"))
			outputDirectory_ = (String) options.valueOf("outdir");
		
		if (options.has("net"))
			networkFile_ = (String) options.valueOf("net");

		if (options.has("dir"))
			isDirected_ = true;
		if (options.has("undir"))
			isDirected_ = false;
		if (options.has("weighted"))
			isWeighted_ = true;

		if (options.has("ref"))
			refNodesFile_ = (String) options.valueOf("ref");

		if (options.has("self"))
			removeSelfLoops_ = false;
		if (options.has("noself"))
			removeSelfLoops_ = true;
		
		// TBD, write a method that checks consistency / if everything has been
		// defined that we need
		checkOptions();
	}

	// ============================================================================
	// PRIVATE METHODS

	/** Display help on console */
	static private void displayHelp() {

		System.out.println("USAGE");
		System.out.println("   java -jar Ngsea.jar <file> -o <file> [OPTIONS]");
		System.out.println("OPTIONS");
		System.out.println("   --help          Display this usage information");
		System.out.println("   --set <file>    The configuration file");
		System.out.println("   --net <file>    The input network file");
		System.out.println("   --dir           Treat network as directed");
		System.out.println("   --undir         Treat network as undirected");
		System.out
				.println("   --self          Allow self-loops in the network");
		System.out
				.println("   --noself        Remove self-loops from the network");
		System.out
				.println("   --ref <file>    File specifying a set of reference nodes");
	}

	
	// ----------------------------------------------------------------------------

	/** TODO Check validity of arguments */
	private void checkOptions() {
		
	}

		
		
	// ----------------------------------------------------------------------------

	/** Defines the command-line arguments */
	private void defineArgs() {

		// The command-line parser
		parser_ = new OptionParser();

		// Display help
		parser_.accepts("help");
		// settingsFile_
		parser_.accepts("set").withRequiredArg();

		// mode_
		parser_.accepts("mode").withRequiredArg().ofType(Integer.class);

		// outputDirectory_
		parser_.accepts("outdir").withRequiredArg();
		
		// networkFile_
		parser_.accepts("net").withRequiredArg();
		// isDirected_
		parser_.accepts("dir");
		parser_.accepts("undir");
		// isWeighted_
		parser_.accepts("weighted");
		// removeSelfLoops_
		parser_.accepts("self");
		parser_.accepts("noself");
		// refNodesFile_
		parser_.accepts("ref").withRequiredArg();
		
		// Example
		// parser_.accepts("cut").withRequiredArg().ofType(Integer.class).defaultsTo(-1);
	}

}
