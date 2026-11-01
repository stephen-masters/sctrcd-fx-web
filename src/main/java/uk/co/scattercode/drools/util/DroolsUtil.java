package uk.co.scattercode.drools.util;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.beanutils.BeanUtils;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.scattercode.beans.BeanMatcher;

/**
 *
 * @author Stephen Masters
 */
public class DroolsUtil {

    private static final Logger log = LoggerFactory.getLogger(DroolsUtil.class);

    private static final BeanMatcher matcher = new BeanMatcher();

    /**
     * This class provides static methods, so we prevent instantiation.
     */
    private DroolsUtil() {}

    public static KieBase createKnowledgeBase(DroolsResource[] resources) {
        return createKnowledgeBase(resources, EventProcessingOption.STREAM);
    }

    /**
     * Creates a new knowledge base using a collection of resources.
     * <p>
     * The resources are written to an in-memory {@link KieFileSystem} under a
     * release id of their own, so that building one knowledge base never
     * replaces another in the shared KIE repository.
     * </p>
     *
     * @param resources
     *            An array of {@link DroolsResource} indicating where the
     *            various resources should be loaded from. These could be
     *            classpath or file resources.
     * @return A new knowledge base.
     */
    public static KieBase createKnowledgeBase(DroolsResource[] resources, EventProcessingOption eventProcessingOption) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        ReleaseId releaseId = ks.newReleaseId("uk.co.scattercode", "kbase-" + UUID.randomUUID(), "1.0.0");
        kfs.generateAndWritePomXML(releaseId);

        int index = 0;
        for (DroolsResource resource : resources) {
            log.info("Resource: " + resource.getType() + ", path type=" + resource.getPathType() + ", path="
                    + resource.getPath());
            Resource kieResource = switch (resource.getPathType()) {
                case CLASSPATH -> ks.getResources().newClassPathResource(resource.getPath());
                case FILE -> ks.getResources().newFileSystemResource(resource.getPath());
            };
            kieResource.setResourceType(resource.getType());
            String fileName = Path.of(resource.getPath()).getFileName().toString();
            kfs.write("src/main/resources/kbase/" + index++ + "/" + fileName, kieResource);
        }

        KieBuilder builder = ks.newKieBuilder(kfs).buildAll();
        if (builder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new IllegalStateException(builder.getResults().toString());
        }

        KieBaseConfiguration conf = ks.newKieBaseConfiguration();
        conf.setOption(eventProcessingOption);

        KieBase knowledgeBase = ks.newKieContainer(releaseId).newKieBase(conf);

        log.info(knowledgeBaseDetails(knowledgeBase));

        return knowledgeBase;
    }

    /**
     * Return a string containing the packages used to build the knowledge base.
     */
    public static String knowledgeBaseDetails(KieBase kbase) {
        if (kbase == null) {
            return "Knowledge Base is null.";
        } else {
            StringBuilder sb = new StringBuilder("Knowledge base built from the following packages:");
            for (KiePackage kp : kbase.getKiePackages()) {
                sb.append("\n    Package: [" + kp.getName() + "]");
                for (Rule rule : kp.getRules()) {
                    sb.append("\n        Rule: [" + rule.getName() + "]");
                }
            }
            return sb.toString();
        }
    }

    public static String objectDetails(Object o) {
        StringBuilder sb = new StringBuilder(o.getClass().getSimpleName());

        try {
            Map<String, String> objectProperties = BeanUtils.describe(o);
            for (Map.Entry<String, String> property : objectProperties.entrySet()) {
                sb.append(", " + property.getKey() + "=\"" + property.getValue() + "\"");
            }
        } catch (IllegalAccessException e) {
            return "IllegalAccessException attempting to parse object.";
        } catch (InvocationTargetException e) {
            return "InvocationTargetException attempting to parse object.";
        } catch (NoSuchMethodException e) {
            return "NoSuchMethodException attempting to parse object.";
        }

        return sb.toString();
    }

    public static Object findInsertedFact(List<ObjectInsertedEvent> insertions, String factType, String[] filters) {
        for (ObjectInsertedEvent event : insertions) {
            Object fact = event.getObject();

            if (factType.equals(fact.getClass().getSimpleName())) {
                if (matcher.matches(fact, filters)) {
                    return fact;
                }
            }
        }
        return null;
    }

    /**
     * Search for an activation by rule name. Note that when using decision
     * tables, the rule name is generated as <code>Row N Rule_Name</code>. This
     * means that we can't just search for exact matches. This method will
     * therefore return true if an activation ends with the ruleName argument.
     *
     * @param ruleName
     *            The name of the rule we're looking for.
     */
    public static boolean ruleFired(List<Activation> activations, String ruleName) {
        for (Activation activation : activations) {
            if (activation.getRuleName().toUpperCase().endsWith(ruleName.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given a list of rule activations and a list of rule names, did all of the
     * rules in the list provided get fired? This is a utility method designed
     * primarily to help with testing.
     *
     * @param ruleNames
     *            The list of rule names we're looking for.
     */
    public static boolean allRulesFired(List<Activation> activations, String[] ruleNames) {
        for (String ruleName : ruleNames) {
            if (!ruleFired(activations, ruleName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a <code>String</code> showing the rule names that were expected
     * to fire, and whether or not they fired, ready to be logged.
     *
     * @param ruleNames
     *            The list of rule names we're looking for.
     */
    public static String prettyRulesFired(List<Activation> activations, String[] ruleNames) {
        StringBuilder sb = new StringBuilder();
        for (String ruleName : ruleNames) {
            sb.append("\n    " + ruleName + " : " + (ruleFired(activations, ruleName) ? "Y" : "N"));
        }
        return sb.toString();
    }
}
