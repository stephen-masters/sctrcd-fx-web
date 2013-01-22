package uk.co.scattercode.drools.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.rule.Rule;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.UrlResource;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initialises and encapsulates the various components required for the rules engine.
 * Includes initialising the knowledge base, creating a stateful session and attaching 
 * listeners for the default events.
 * 
 * @author Stephen Masters
 */
public class KnowledgeEnvironment {

    private static Logger log = LoggerFactory.getLogger(KnowledgeEnvironment.class);

    private DroolsResource[] resources;

    private KnowledgeBase knowledgeBase;
    private StatefulKnowledgeSession knowledgeSession;
    private TrackingAgendaEventListener agendaEventListener;
    private TrackingWorkingMemoryEventListener workingMemoryEventListener;

    /**
     * Constructor supporting setting up a knowledge environment using just a
     * list of resources, which may be local. Particularly useful when testing
     * DRL files.
     * 
     * @param resources
     */
    public KnowledgeEnvironment(DroolsResource[] resources) {
        initialise(resources);
    }

    /**
     * Constructor.
     * 
     * @param url The URL of the package via the Guvnor REST API.
     */
    public KnowledgeEnvironment(String url) {
        initialise(url);
    }

    /**
	 * This constructor sets up a user name and password. Handy if you're
	 * connecting to Guvnor and it's locked down.
	 * 
	 * @param url The URL of the package via the Guvnor REST API.
	 * @param username The Guvnor user name.
	 * @param password The Guvnor password.
	 */
    public KnowledgeEnvironment(String url, String username, String password) {
        initialise(url, username, password);
    }

    /**
	 * Initialises the knowledge environment by downloading the package from the
	 * Guvnor REST interface, at the location defined in the URL.
	 * 
	 * @param url The URL of the package via the Guvnor REST API.
	 */
    public void initialise(String url) {
        this.resources = new DroolsResource[] { 
            new DroolsResource(url,
                    ResourcePathType.URL, 
                    ResourceType.PKG
        )};
        initialise();
    }

    /**
     * Initialises the knowledge environment by downloading the package from the
	 * Guvnor REST interface, at the location defined in the URL.
	 * 
     * @param url The URL of the package via the Guvnor REST API.
	 * @param username The Guvnor user name.
	 * @param password The Guvnor password.
     */
    public void initialise(String url, String username, String password) {
        this.resources = new DroolsResource[] { 
                new DroolsResource(url, 
                        ResourcePathType.URL, 
                        ResourceType.PKG, 
                        username, 
                        password
        )};
        initialise();
    }

    /**
	 * Initialises the knowledge environment with multiple
	 * {@link DroolsResource} locations.
	 * 
	 * @param resources An array of {@link DroolsResource}.
	 */
    public void initialise(DroolsResource[] resources) {
        this.resources = resources;
        initialise();
    }

    /**
	 * Initialises the knowledge environment with multiple
	 * {@link DroolsResource} locations, which should have been defined
	 * previously in the constructor.
	 */
    public void initialise() {
        log.info("Initialising KnowledgeEnvironment with resources: " + this.resources);
        this.knowledgeBase = DroolsUtil.createKnowledgeBase(
                this.resources, 
                EventProcessingOption.STREAM);
        
        // Log a description of the new knowledge base.
        log.info(toString());
        
        initialiseSession();
    }
    
    /**
	 * Starts up a new stateless session, and attaches a number of working
	 * memory listeners.
	 */
    public void initialiseSession() {
        log.info("Initialising session...");
        if (this.knowledgeSession == null) {
            this.knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
            this.agendaEventListener = new TrackingAgendaEventListener();
            this.knowledgeSession.addEventListener(this.agendaEventListener);
            this.workingMemoryEventListener = new TrackingWorkingMemoryEventListener();
            this.knowledgeSession.addEventListener(this.workingMemoryEventListener);
        } else {
            retractAll();
            clearListeners();
        }
    }
    
    /**
	 * Remove the existing working memory listeners, and set up some fresh ones.
	 */
    public void clearListeners() {
        this.knowledgeSession.removeEventListener(this.agendaEventListener);
        this.knowledgeSession.removeEventListener(this.workingMemoryEventListener);

        this.agendaEventListener = new TrackingAgendaEventListener();
        this.workingMemoryEventListener = new TrackingWorkingMemoryEventListener();

        this.knowledgeSession.addEventListener(this.agendaEventListener);
        this.knowledgeSession.addEventListener(this.workingMemoryEventListener);
    }
    
    public FactHandle insert(Object o) {
        return this.knowledgeSession.insert(o);
    }
    
    public List<FactHandle> insert(Collection<Object> facts) {
        List<FactHandle> handles = new ArrayList<FactHandle>();
        for (Object fact : facts) {
            handles.add(this.knowledgeSession.insert(fact));
        }
        return handles;
    }
    
    /**
     * Retracts all fact handles from working memory.
     */
    public void retractAll() {
        retractAll(this.knowledgeSession.getFactHandles());
    }
    
    public void retractAll(ObjectFilter filter) {
        retractAll(this.knowledgeSession.getFactHandles(filter));
    }
    
    public void retractAll(Collection<FactHandle> handles) {
        for (FactHandle handle : handles) {
            retract(handle);
        }
    }
    
    public void retract(FactHandle handle) {
        this.knowledgeSession.retract(handle);
    }
    
    public void update(FactHandle handle, Object o) {
        this.knowledgeSession.update(handle, o);
    }
    
    public void fireAllRules() {
        this.knowledgeSession.fireAllRules();
    }

    /**
     * Attaches an {@link AgendaEventListener} to the session.
     * 
     * @param listener The listener to be attached.
     */
    public void addEventListener(AgendaEventListener listener) {
        knowledgeSession.addEventListener(listener);
    }
    
    /**
     * Disconnects an {@link AgendaEventListener} from the session.
     * 
     * @param listener The listener to be disconnected.
     */
    public void removeEventListener(AgendaEventListener listener) {
        knowledgeSession.removeEventListener(listener);
    }
    
    /**
     * Attaches a {@link WorkingMemoryEventListener} to the session.
     * 
     * @param listener The listener to be attached.
     */
    public void addEventListener(WorkingMemoryEventListener listener) {
        knowledgeSession.addEventListener(listener);
    }
    
    /**
     * Disconnects a {@link WorkingMemoryEventListener} from the session.
     * 
     * @param listener The listener to be disconnected.
     */
    public void removeEventListener(WorkingMemoryEventListener listener) {
        knowledgeSession.removeEventListener(listener);
    }
    
    public List<Activation> getActivationList() {
        return this.agendaEventListener.getActivationList();
    }

    /**
     * 
     * @return A String detailing the packages and rules in this knowledge base.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (KnowledgePackage p : knowledgeBase.getKnowledgePackages()) {
            sb.append("\n  Package : " + p.getName());
            for (Rule r : p.getRules()) {
                sb.append("\n    Rule: " + r.getName());
            }
        }
        return "Knowledge base built with packages: " + sb.toString();
    }
    
    /**
     * Iterates through the facts currently in working memory, and logs their details.
     * 
     * @param session The session to search for facts.
     */
    public void printFacts(StatefulKnowledgeSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n************************************************************");
        sb.append("\nThe following facts are currently in the system...");
        for (Object fact : session.getObjects()) {
            sb.append("\n\nFact: " + DroolsUtil.objectDetails(fact));
        }
        sb.append("\n************************************************************\n");
        log.info(sb.toString());
    }

    public DroolsResource[] getResources() {
        return resources;
    }

    public void setResources(DroolsResource[] resources) {
        this.resources = resources;
    }

    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public StatefulKnowledgeSession getKnowledgeSession() {
        return knowledgeSession;
    }

    public void setKnowledgeSession(StatefulKnowledgeSession knowledgeSession) {
        this.knowledgeSession = knowledgeSession;
    }

    public TrackingAgendaEventListener getAgendaEventListener() {
        return agendaEventListener;
    }

    public void setAgendaEventListener(
            TrackingAgendaEventListener agendaEventListener) {
        this.agendaEventListener = agendaEventListener;
    }

    public TrackingWorkingMemoryEventListener getWorkingMemoryEventListener() {
        return workingMemoryEventListener;
    }

    public void setWorkingMemoryEventListener(
            TrackingWorkingMemoryEventListener workingMemoryEventListener) {
        this.workingMemoryEventListener = workingMemoryEventListener;
    }

}
