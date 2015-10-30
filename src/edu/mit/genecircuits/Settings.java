/*
Copyright (c) 2013 Daniel Marbach
 
We release this software open source under an MIT license (see below). If this
software was useful for your scientific work, please cite our paper available at:
http://compbio.mit.edu/flynet

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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.Well19937c;



/** 
 * Offers global parameters (settings) and functions used by all classes of the
 * ngsea package.
 * 
 * Code adapted from GnwSettings.java by Thomas Schaffter and Daniel Marbach (gnw.sf.net)
 */
public class Settings {	
	
	/** The configuration file with the settings (leave empty for default settings) */
	static public String settingsFile_ = null;
	/** The properties (settings file) */
	static private Properties set_ = null;
	
	/** Colt Mersenne Twister random engine (should be used by all other random number generators) */
	static public MersenneTwister mersenneTwisterRng_ = null;
	/** Apache Commons random engine */
	static public Well19937c wellRng_ = null;
	/** Java random engine */
	static public Random jdkRng_ = null;

	// ----------------------------------------------------------------------------
	// VARIOUS
	
	/** Current version of N-GSEA */
	static public String ngseaVersion_ = "0.1 Alpha";
	/** Mode: 1 => Network analysis; 2 => Enrichment analysis */
	static public int mode_ = -1;
	/** Seed for the random number generator. Set to -1 to use current time */
	static public int randomSeed_ = -1;
	/** Output directory to save stuff */
	static public String outputDirectory_ = "";
	/** Compress output files (gzip) */
	static public boolean compressFiles_ = true;

	// ----------------------------------------------------------------------------
	// CONSTRUCT REGULATORY CIRCUITS

	// INPUT FILES

	/** Suffix used for all files */
	static public String suffix_ = null;
	/** Base directory of circuit files */
	static public String circuitDir_ = null;
	/** Circuit output directory */
	static public String circuitOutDir_ = null;

	/** tf---promoter file prefix */
	static public String tfPromoterFile_ =  null;
	/** promoter---transcript file prefix */
	static public String promoterTranscriptFile_ = null;
	/** Promoter expression file prefix */
	static public String promoterExprFile_ = null;

	/** tf---enhancer file prefix */
	static public String tfEnhancerFile_ =  null;
	/** enhancer---transcript file prefix */
	static public String enhancerTranscriptFile_ = null;
	/** Enhancer expression file prefix */
	static public String enhancerExprFile_ = null;
	/** Transcript expression file prefix */
	static public String transcriptExprFile_ = null;

	
	// ----------------------------------------------------------------------------
	// NETWORK PROPERTIES

	// INPUT NETWORK
	/** The input network file */
	static public String networkFile_ = null;
	/** Delimiter used to separate columns (default 'tab' */
	static public String networkFileDelim_ = null;  
	/** Defines if the network should be interpreted as directed or undirected */
	static public boolean isDirected_ = true;
	/** Defines if self loops should be removed from the network */
	static public boolean removeSelfLoops_ = false;
	/** Set true to treat the network as weighted */
	static public boolean isWeighted_ = false;
	/** Threshold for including weighted edges */
	static public double threshold_ = 0;
	/** Exclude "super-hubs" that connect to more than the given fraction of genes (set 1 to include all) */
	static public double superHubThreshold_ = 1;
	/** Optional file specifying a set of reference nodes */
	static public String refNodesFile_ = null;

	// BASIC NETWORK PROPERTIES
	/** Node degree (directed networks, also indegree and outdegree) */
	static public boolean computeDegree_ = true;
	/** Node betweenness centrality (edge directionality observed for directed networks) */
	static public boolean computeBetweenness_ = false;
	/** Node clustering coefficient (edge directionality observed for directed networks) */
	static public boolean computeClusteringCoefficient_ = false;
	/** For each node, distance to all other nodes (or all reference nodes) and closeness centrality */
	static public boolean computeShortestPathLengths_ = false;

	// KERNELS
	/** P-step random walk kernel (Smola & Kondor, 2003) */
	static public boolean computePstepKernel_ = false;
	/** alpha parameter of p-step random walk kernel (alpha >= 2) */
	static public ArrayList<Double> pstepKernelAlpha_ = null;
	/** Number of steps p of random walk kernel (p >= 1) */
	static public ArrayList<Integer> pstepKernelP_ = null;
	/** Normalize the kernel matrix (divide by the max) */
	static public boolean pstepKernelNormalize_ = true;

	// OUTPUT FILES
	/** A suffix/ending that is appended to all output files for this run (use to distinguish output files from multiple runs) */
	static public String outputSuffix_ = "";
	/** Export all computed pairwise node properties (e.g., similarity, distance matrices) */
	static public boolean exportPairwiseNodeProperties_ = true;
	/** Export all computed node properties (e.g., avg. similarity, distance for each node) */
	static public boolean exportNodeProperties_ = true;


	
	// ============================================================================
	// PUBLIC METHODS
	
	/** Initialize settings */
	static public void initialize() {
		
		initializeRandomNumberGenerators();
	}
	
	// ----------------------------------------------------------------------------
	
	/** Load and initialize settings */
	static public void loadSettings() {
		
		try {
			InputStream in;
			if (settingsFile_ == null || settingsFile_.compareTo("") == 0) {
				GcMain.println("- No settings file specified");
				GcMain.println("- Loading default settings\n");
				in = Settings.class.getClassLoader().getResourceAsStream("edu/mit/genecircuits/settings.txt");
			} else {
				GcMain.println("- Loading settings file: " + settingsFile_ + "\n");
				in = new FileInputStream(settingsFile_);
			}
			set_ = new Properties();
			set_.load(new InputStreamReader(in));
			
			setParameterValues();
			
		} catch (Exception e) {
			GcMain.warning(e.getMessage());
			GcMain.error("Failed to load settings file (a parameter may be missing or malformed): " + settingsFile_);
		}
		
		// Reinitialize the random number generators
		initializeRandomNumberGenerators();		
	}
	
	
	// ----------------------------------------------------------------------------
	
	/** Get folder of the default ngsea directory (in home) */
	public File getGseaDirectory() {
		
		File folder = new File(ngseaDirectoryPath());
		return folder;
	}
	
	
	// ----------------------------------------------------------------------------
	
	/** Get file associated to default settings file */
	public File getCustomNgseaSettings() {
		
		File file = new File(personalNgseaSettingsPath());
		return file;
	}
	
	
	// ----------------------------------------------------------------------------
	
	/** Get path to default ngsea directory (in home) */
	public String ngseaDirectoryPath() {
		
		return System.getProperty("user.home")
				+ System.getProperty("file.separator")
				+ "ngsea";
	}
	
	
	// ----------------------------------------------------------------------------
	
	/** Get path to default settings file */
	public String personalNgseaSettingsPath() {
		
		return ngseaDirectoryPath()
				+ System.getProperty("file.separator")
				+ "settings.txt";
	}
	
	
	// ----------------------------------------------------------------------------
	
	/** Return true if a custom default ngsea settings file exists */
	public boolean personalNgseaSettingsExist() {
		
		return (new File(personalNgseaSettingsPath())).exists();
	}
	
	
	// ----------------------------------------------------------------------------
	
	/**
	 * Set the user path. Could be with or without "/" terminal.
	 * @param absPath Absolute path
	 */
	public void setOutputDirectory(String absPath) {
		
		outputDirectory_ = absPath;
		String sep = System.getProperty("file.separator");
		if (outputDirectory_.charAt(outputDirectory_.length()-1) != sep.charAt(0))
			outputDirectory_ += sep;
	}
	
	
	// ----------------------------------------------------------------------------

	/** Create new instances for the random number generators, initialize with randomSeed_ */
	static public void initializeRandomNumberGenerators() {
		
		if (randomSeed_ == -1) {
			mersenneTwisterRng_ = new MersenneTwister();
			wellRng_ = new Well19937c();
			jdkRng_ = new Random();
		} else {
			mersenneTwisterRng_ = new MersenneTwister(randomSeed_);
			wellRng_ = new Well19937c(randomSeed_);
			jdkRng_ = new Random(randomSeed_);
		}
		
		//uniformDistribution_ = new Uniform(mersenneTwister_);
		//normalDistribution_ = new Normal(0, 1, mersenneTwister_); // mean 0, stdev 1
	}
	

	// ============================================================================
	// PRIVATE METHODS

	/** Set ngsea parameters based on the loaded properties */
	static private void setParameterValues() throws Exception {

		// VARIOUS
		mode_ = getSettingInt("mode");
		randomSeed_ = getSettingInt("randomSeed");
		outputDirectory_ = getSetting("outputDirectory");
		if (outputDirectory_.equals("")) 
			outputDirectory_ = System.getProperty("user.dir");

		// INPUT FILES
		suffix_ = getSetting("suffix");
		circuitDir_ = getSetting("circuitDir");
		circuitOutDir_ = getSetting("circuitOutDir");

		tfPromoterFile_ =  getSetting("tfPromoterFile");
		promoterTranscriptFile_ = getSetting("promoterTranscriptFile");
		promoterExprFile_ = getSetting("promoterExprFile");

		tfEnhancerFile_ =  getSetting("tfEnhancerFile");
		enhancerTranscriptFile_ = getSetting("enhancerTranscriptFile");
		enhancerExprFile_ = getSetting("enhancerExprFile");
		transcriptExprFile_ = getSetting("transcriptExprFile");
		
		// INPUT NETWORK
		networkFile_ = getSetting("networkFile");
		networkFileDelim_ = getSetting("networkFileDelim");
		isDirected_ = getSettingBoolean("isDirected");
		removeSelfLoops_ = getSettingBoolean("removeSelfLoops");
		isWeighted_ = getSettingBoolean("isWeighted");
		threshold_ = getSettingDouble("threshold");
		superHubThreshold_ = getSettingDouble("superHubThreshold");
		refNodesFile_ = getSetting("refNodesFile");

		// OUTPUT FILES
		outputSuffix_ = getSetting("outputSuffix");
		exportPairwiseNodeProperties_ = getSettingBoolean("exportPairwiseNodeProperties");
		exportNodeProperties_ = getSettingBoolean("exportNodeProperties");
		compressFiles_ = getSettingBoolean("compressFiles");
		
		// BASIC NETWORK PROPERTIES
		computeDegree_ = getSettingBoolean("computeDegree");
		computeBetweenness_ = getSettingBoolean("computeBetweenness");
		computeClusteringCoefficient_ = getSettingBoolean("computeClusteringCoefficient");
		
		// SHORTEST PATHS
		computeShortestPathLengths_ = getSettingBoolean("computeShortestPathLengths");

		// KERNELS
		computePstepKernel_ = getSettingBoolean("computePstepKernel");
		pstepKernelAlpha_ = getSettingDoubleArray("pstepKernelAlpha", false);
		pstepKernelP_ = getSettingIntArray("pstepKernelP", true);
		pstepKernelNormalize_ = getSettingBoolean("pstepKernelNormalize");
		
	}
	
	
	// ----------------------------------------------------------------------------

	/** Get the string value of a parameter from the setting file */
	static private String getSetting(String param) {
		
		String value = set_.getProperty(param);
		if (value == null)
			GcMain.error("Parameter not found in setting file: " + param);
		
		return value; 
	}

	
	// ----------------------------------------------------------------------------

	/** Get the integer value of a parameter from the setting file */
	static private int getSettingInt(String param) {
		return Integer.valueOf(getSetting(param)); 
	}

	/** Get the double value of a parameter from the setting file */
	static private double getSettingDouble(String param) {
		return Double.valueOf(getSetting(param)); 
	}

	// ----------------------------------------------------------------------------

	/** Parse a boolean property */
	static private boolean getSettingBoolean(String name) {
		
		String value = getSetting(name);
		if (value.equals("1") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("t"))
			return true;
		else if (value.equals("0") || value.equalsIgnoreCase("false") || value.equalsIgnoreCase("f"))
			return false;
		else
			throw new IllegalArgumentException("Parse error for boolean parameter '" + name + "': expected '1' or '0', found '" + value + "'");
	}

	
	// ----------------------------------------------------------------------------

	/** Parse an int array property */
	static private ArrayList<Integer> getSettingIntArray(String name, boolean positiveSorted) {
		
		String[] propStr = getSetting(name).split(",");
		ArrayList<Integer> prop = new ArrayList<Integer>();
		
		if (propStr.length == 1 && propStr[0].compareTo("") == 0)
			return prop;

		for (int i=0; i<propStr.length; i++)
			prop.add(Integer.valueOf(propStr[i]));
			
		if (positiveSorted && !GcUtils.posIntIncreasing(prop))
			GcMain.error("Error parsing settings file, " + name + " has to be an ordered list of positive integers, given in increasing order");
		
		return prop;
	}


	// ----------------------------------------------------------------------------

	/** Parse an int array property */
	static private ArrayList<Double> getSettingDoubleArray(String name, boolean positiveSorted) {
		
		String[] propStr = getSetting(name).split(",");
		ArrayList<Double> prop = new ArrayList<Double>();
		
		if (propStr.length == 1 && propStr[0].compareTo("") == 0)
			return prop;

		for (int i=0; i<propStr.length; i++)
			prop.add(Double.valueOf(propStr[i]));
			
		if (positiveSorted && !GcUtils.posDoubleIncreasing(prop))
			GcMain.error("Error parsing settings file, " + name + " has to be an ordered list of positive numbers, given in increasing order");
		
		return prop;
	}

	
	// ----------------------------------------------------------------------------

	/** Parse a string array property */
	@SuppressWarnings("unused")
	static private String[] getStringArraySetting(Properties set, String name) {
		
		return set.getProperty(name).split(",");
	}

}
