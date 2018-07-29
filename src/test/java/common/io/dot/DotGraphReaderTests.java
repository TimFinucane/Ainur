package common.io.dot;

import common.graph.Edge;
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
        String text = "\t0\t[Weight=1];";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        //Act
        GraphReader reader = new DotGraphReader(stream);
        Graph graph = reader.read();

        //Assert
        Node entryNode = graph.getEntryPoints().get(0);
        Assert.assertEquals(entryNode.getLabel(), "0");
        Assert.assertEquals(entryNode.getComputationCost(), 1);
    }

    @Test
    public void testTwoNodeGraphWithEdge() {

        // Arrange
        String text = "\t0\t[Weight=1];\n" +
                "\t1\t[Weight=2];\n" +
                "\t0 -> 1\t[Weight=3];";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        //Act
        GraphReader reader = new DotGraphReader(stream);
        Graph graph = reader.read();

        //Assert
        Node firstNode = graph.getEntryPoints().get(0);
        Edge firstNodeFirstEdge = graph.getOutgoingEdges(firstNode).get(0);
        Node secondNode = firstNodeFirstEdge.getDestinationNode();

        Assert.assertEquals("0", firstNode.getLabel());
        Assert.assertEquals(1, firstNode.getComputationCost());
        Assert.assertEquals("1", secondNode.getLabel());
        Assert.assertEquals(2, secondNode.getComputationCost());
        Assert.assertEquals(firstNode, firstNodeFirstEdge.getOriginNode());
        Assert.assertEquals(secondNode, firstNodeFirstEdge.getDestinationNode());
        Assert.assertEquals(3, firstNodeFirstEdge.getCost());

    }

    @Test
    public void testTextWithNoise() {

        String text = "digraph \"OutTree-Balanced-MaxBf-3_Nodes_11_CCR_0.1_WeightType_Random\" " +
                "\t0\t [Weight=1];" +
                "{";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        //Act
        GraphReader reader = new DotGraphReader(stream);
        Graph graph = reader.read();

        //Assert
        Node entryNode = graph.getEntryPoints().get(0);
        Assert.assertEquals(entryNode.getLabel(), "0");
        Assert.assertEquals(entryNode.getComputationCost(), 1);

    }

    @Test
    public void testNodeWithTwoEdges() {

        // Arrange
        String text = "0\t [Weight=1];\n" +
                "\t1\t [Weight=2];" +
                "\t2\t [Weight=3];" +
                "\t0 -> 1\t[Weight=4];" +
                "\t0 -> 2\t[Weight=5];";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        //Act
        GraphReader reader = new DotGraphReader(stream);
        Graph graph = reader.read();

        //Assert
        Node firstNode = graph.getEntryPoints().get(0);
        Edge firstNodeFirstEdge = graph.getOutgoingEdges(firstNode).get(0);
        Edge firstNodeSecondEdge = graph.getOutgoingEdges(firstNode).get(1);
        Node secondNode = firstNodeFirstEdge.getDestinationNode();
        Node thirdNode = firstNodeSecondEdge.getDestinationNode();
        Assert.assertEquals(4, firstNodeFirstEdge.getCost());
        Assert.assertEquals(5, firstNodeSecondEdge.getCost());
        Assert.assertEquals("1", secondNode.getLabel());
        Assert.assertEquals(2, secondNode.getComputationCost());
        Assert.assertEquals("2", thirdNode.getLabel());
        Assert.assertEquals(3, thirdNode.getComputationCost());

    }

}