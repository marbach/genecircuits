package edu.mit.genecircuits.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.mit.genecircuits.net.test.CircuitBuilderTest;

@RunWith(Suite.class)
//@SuiteClasses({ NetworkTest.class, AnalyzerBasicPropertiesTest.class, AnalyzerShortestPathsTest.class, 
//	AnalyzerPstepKernelTest.class })
@SuiteClasses({ CircuitBuilderTest.class })
public class AllTests {

}
