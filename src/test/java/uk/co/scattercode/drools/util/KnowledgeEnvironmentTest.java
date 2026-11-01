package uk.co.scattercode.drools.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.scattercode.drools.util.DroolsTestUtil.assertFactIsInWorkingMemory;
import static uk.co.scattercode.drools.util.DroolsTestUtil.assertRuleFired;

import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ObjectFilter;
import uk.co.scattercode.beans.BeanPropertyFilter;

class KnowledgeEnvironmentTest {

    public static final ObjectFilter MESSAGE_FILTER =
            object -> object.getClass().getSimpleName().equals("Message");

    private static KnowledgeEnvironment newEnvironment() {
        return new KnowledgeEnvironment(new DroolsResource[] {
            new DroolsResource(
                    "sctrcd/drools/util/KnowledgeEnvironmentTest.drl", ResourcePathType.CLASSPATH, ResourceType.DRL)
        });
    }

    @Test
    void shouldReloadOnCommand() {
        KnowledgeEnvironment kenv = newEnvironment();

        assertEquals(0, kenv.getFactHandles(MESSAGE_FILTER).size());
        kenv.insert(new Message("Hello Droolers!"));
        assertEquals(1, kenv.getFactHandles(MESSAGE_FILTER).size());
        kenv.initialise();
        assertEquals(0, kenv.getFactHandles(MESSAGE_FILTER).size());
        kenv.insert(new Message("Hello Droolers!"));
        assertEquals(1, kenv.getFactHandles(MESSAGE_FILTER).size());
    }

    @Test
    void shouldFireTheRuleAndModifyTheFact() {
        KnowledgeEnvironment kenv = newEnvironment();

        kenv.insert(new Message("Hello Droolers!"));
        kenv.fireAllRules();

        assertRuleFired(kenv, "Say hello");
        assertFactIsInWorkingMemory(kenv, "Message", new BeanPropertyFilter("text", "Hello, World!"));
    }

    public static class Message {
        private String text;

        public Message(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
