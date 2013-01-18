package uk.co.scattercode.drools.util;

import org.drools.runtime.rule.FactHandle;
import org.junit.After;
import org.junit.Before;

import uk.co.scattercode.drools.util.DroolsResource;
import uk.co.scattercode.drools.util.KnowledgeEnvironment;

/**
 * Extend this class within your unit tests so that you can gain simplified
 * access to Drools rules evaluations.
 * 
 * @author Stephen Masters
 */
public abstract class AbstractStatefulRulesService {

	protected KnowledgeEnvironment knowledgeEnvironment 
			= new KnowledgeEnvironment(getResources());

	/**
	 * The test knowledge base will be initialised using these resources.
	 * Extending classes need to implement this method to return an array of
	 * {@link DroolsResource} references. These can be anything from DRL files
	 * on the class path to Guvnor packages.
	 * 
	 * @return An array of {@link DroolsResource} references 
	 */
	protected abstract DroolsResource[] getResources();

	/**
	 * Prior to each test, a new stateful knowledge session will be initialised,
	 * and various working memory listeners will be attached to help track your
	 * rules evaluations.
	 */
	@Before
	public void startKnowledgeSession() {
		knowledgeEnvironment.initialiseSession();
	}

	/**
	 * Nothing is currently done after each test, as the knowledge base is fully
	 * initialised prior to each test.
	 */
	@After
	public void endKnowledgeSession() {
	}

	/**
	 * Insert a POJO fact into working memory.
	 * 
	 * @param o
	 *            A {@link FactHandle} to the object in working memory.
	 */
	protected FactHandle insert(Object o) {
		return knowledgeEnvironment.knowledgeSession.insert(o);
	}

	/**
	 * Update a fact which is already in working memory.
	 * 
	 * @param factHandle
	 *            A {@link FactHandle} to the object which is already in working
	 *            memory.
	 * @param o
	 *            The new/updated object.
	 */
	protected void update(FactHandle factHandle, Object o) {
		knowledgeEnvironment.knowledgeSession.update(factHandle, o);
	}
    
	/**
	 * Retract a fact from working memory.
	 * 
	 * @param handle
	 *            A handle on to the fact to be retracted as returned by the
	 *            <code>insert(Object)</code> method.
	 */
    protected void retract(FactHandle handle) {
    	knowledgeEnvironment.knowledgeSession.retract(handle);
    }
    
    /**
	 * Fire all rules in the knowledge base. Generally you will be looking to
	 * insert a number of facts and then call this method to evaluate them.
	 */
    protected void fireAllRules() {
    	knowledgeEnvironment.knowledgeSession.fireAllRules();
    }

}
