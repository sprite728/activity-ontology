package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.input.WordNetInputParser;
import io.mem0r1es.activitysubsumer.utils.Cons;
import org.testng.annotations.Test;

/**
 * @author Ivan Gavrilović
 */
public class ParseInputTest {
    @Test
    public void parseInput(){
        WordNetInputParser parser = new WordNetInputParser();
        parser.reduceInput(Cons.RDF_WN, Cons.OUTPUT_RDF_WN);
    }
}
