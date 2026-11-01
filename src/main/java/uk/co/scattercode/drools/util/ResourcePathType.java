package uk.co.scattercode.drools.util;

/**
 * A subset of 'path-based' resource types that can be defined by a String for
 * adding to a Drools resource builder.
 * <p>
 * Drools 5 could also load a resource from a URL, which was how a knowledge
 * base was fetched from Guvnor's REST API. Guvnor is long gone and Drools 10
 * no longer offers URL resources, so only classpath and file paths remain.
 * </p>
 *
 * @author Stephen Masters
 */
public enum ResourcePathType {
    CLASSPATH,
    FILE;
}
