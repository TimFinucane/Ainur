package common.io.dot;

import common.graph.Graph;
import common.graph.Node;
import io.GraphReader;
import io.dot.DotGraphReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DotGraphReaderTests {

    @Test
    public void testSingleNodeGraph() {

        // Arrange
        String text = "0   [Weight=1];";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        //Act
        GraphReader reader = new DotGraphReader(stream);
        Graph graph = reader.read();

        //Assert
        Node entryNode = graph.getEntryPoints().get(0);
        Assert.assertEquals(entryNode.getLabel(), "0");
        Assert.assertEquals(entryNode.getComputationCost(), 1);
    }

}
