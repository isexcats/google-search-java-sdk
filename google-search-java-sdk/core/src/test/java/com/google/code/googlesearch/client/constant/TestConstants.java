/**
 *
 */
package com.google.code.googlesearch.client.constant;
import java.io.IOException;
import java.util.Properties;

/**
 * The Class TestConstants.
 */
public final class TestConstants {

    /** The Constant TEST_CONSTANTS_FILE. */
    public static final String TEST_CONSTANTS_FILE = "TestConstants.properties";

    /** The Constant testConstants. */
    private static final Properties testConstants = new Properties();

    static {
        try {
            testConstants.load(TestConstants.class.getResourceAsStream(TEST_CONSTANTS_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /** The Constant STACK_OVERFLOW_TEST_API_KEY. */
    public static final String TEST_API_KEY =
        testConstants.getProperty("com.google.code.google.search.client.applicationId");
    
    public static final String TEST_REFERRER =
        testConstants.getProperty("com.google.code.google.search.client.referrer");
    
    public static final String TEST_QUERY =
        testConstants.getProperty("com.google.code.google.search.client.testQuery");
    
    /**
     * Instantiates a new test constants.
     */
    private TestConstants() {}
}
