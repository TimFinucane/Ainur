package io.dot;

import common.graph.Edge;
import common.graph.Graph;
import common.graph.Node;
import io.GraphReader;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public class DotGraphReaderTests {

    /**
     * Tests a graph with single node only, ensures minimum size non empty graph can be parsed.
     */
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
        assertEquals(entryNode.getLabel(), "0");
        assertEquals(entryNode.getComputationCost(), 1);
    }

    /**
     * Tests that edges work correctly in referencing origin and destination nodes.
     */
    @Test
    public void testTwoNodeGraphWithEdge() {

        // Arrange
        String text = "\t0\t[Weight=1];\n" +
                "\t1\t[Weight=2];\n" +
                "\t0\t->\t1\t[Weight=3];";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        //Act
        GraphReader reader = new DotGraphReader(stream);
        Graph graph = reader.read();

        //Assert
        Node firstNode = graph.getEntryPoints().get(0);
        Edge firstNodeFirstEdge = graph.getOutgoingEdges(firstNode).get(0);
        Node secondNode = firstNodeFirstEdge.getDestinationNode();

        assertEquals("0", firstNode.getLabel());
        assertEquals(1, firstNode.getComputationCost());
        assertEquals("1", secondNode.getLabel());
        assertEquals(2, secondNode.getComputationCost());
        assertEquals(firstNode, firstNodeFirstEdge.getOriginNode());
        assertEquals(secondNode, firstNodeFirstEdge.getDestinationNode());
        assertEquals(3, firstNodeFirstEdge.getCost());

    }

    /**
     * Tests that file with header content can still be parsed.
     */
    @Test
    public void testTextWithHeader() {

        String text = "digraph \"OutTree-Balanced-MaxBf-3_Nodes_11_CCR_0.1_WeightType_Random\" { " +
                "\t0\t [Weight=1];" +
                "}";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        //Act
        GraphReader reader = new DotGraphReader(stream);
        Graph graph = reader.read();

        //Assert
        Node entryNode = graph.getEntryPoints().get(0);
        assertEquals(entryNode.getLabel(), "0");
        assertEquals(entryNode.getComputationCost(), 1);

    }

    /**
     * Tests that graph containing node with two outgoing edges is correctly parsed.
     */
    @Test
    public void testNodeWithTwoEdges() {

        // Arrange
        String text = "0\t [Weight=1];\n" +
                "\t1\t [Weight=2];" +
                "\t0 -> 1\t[Weight=4];" +
                "\t2\t [Weight=3];" +
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
        assertEquals(4, firstNodeFirstEdge.getCost());
        assertEquals(5, firstNodeSecondEdge.getCost());
        assertEquals("1", secondNode.getLabel());
        assertEquals(2, secondNode.getComputationCost());
        assertEquals("2", thirdNode.getLabel());
        assertEquals(3, thirdNode.getComputationCost());

    }

    /**
     * Tests graph containing node with both ingoing and outgoing edges can be parsed.
     */
    @Test
    public void testTwoNodesDeepGraph() {

        // Arrange
        String text = "\t0\t[Weight=1];" +
                "\t1\t[Weight=2];" +
                "\t2\t[Weight=3];" +
                "\t0 -> 1\t[Weight=4];" +
                "\t1 -> 2\t[Weight=5];";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        //Act
        GraphReader reader = new DotGraphReader(stream);
        Graph graph = reader.read();

        // Assert
        Node firstNode = graph.getEntryPoints().get(0);
        Edge firstNodeFirstEdge = graph.getOutgoingEdges(firstNode).get(0);
        Node secondNode = firstNodeFirstEdge.getDestinationNode();
        Edge secondNodeFirstEdge = graph.getOutgoingEdges(secondNode).get(0);
        Node thirdNode = secondNodeFirstEdge.getDestinationNode();
        assertEquals("0", firstNode.getLabel());
        assertEquals("1", secondNode.getLabel());
        assertEquals("2", thirdNode.getLabel());
        assertEquals(4, firstNodeFirstEdge.getCost());
        assertEquals(5, secondNodeFirstEdge.getCost());
        assertEquals(firstNode, firstNodeFirstEdge.getOriginNode());
        assertEquals(secondNode, firstNodeFirstEdge.getDestinationNode());
        assertEquals(secondNode, secondNodeFirstEdge.getOriginNode());
        assertEquals(thirdNode, secondNodeFirstEdge.getDestinationNode());

    }

    /**
     * Test that graph with multiple nodes with no incoming edge can be parsed.
     */
    @Test
    public void testGraphWithMultipleEntryPoints() {

        // Arrange
        String text = "\t0\t[Weight=1];" +
                "\t1\t[Weight=2];" +
                "\t2\t[Weight=3];" +
                "\t1 -> 2\t[Weight=4];";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        //Act
        GraphReader reader = new DotGraphReader(stream);
        Graph graph = reader.read();

        // Assert
        Node firstNode = graph.getEntryPoints().get(0);
        Node secondNode = graph.getEntryPoints().get(1);
        Edge secondNodeFirstEdge = graph.getOutgoingEdges(secondNode).get(0);
        Node thirdNode = secondNodeFirstEdge.getDestinationNode();
        assertEquals("0", firstNode.getLabel());
        assertEquals("1", secondNode.getLabel());
        assertEquals("2", thirdNode.getLabel());
        assertEquals(secondNode, secondNodeFirstEdge.getOriginNode());
        assertEquals(thirdNode, secondNodeFirstEdge.getDestinationNode());

    }

    /**
     * Tests that semantically invalid graph correctly throws UncheckedIOException.
     */
    @Test
    public void testInvalidGraph() {

        // Arrange
        String text = "\t0\t[Weight=1];" +
                "\t1\t[Weight=2];" +
                "\t2\t[Weight=3];" +
                "\t0 -> 3\t[Weight=4];" +
                "\t1 -> 2\t[Weight=5];";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        //Act / Assert
        GraphReader reader = new DotGraphReader(stream);
        try {
            Graph graph = reader.read();
            fail();
        } catch (IllegalArgumentException e) { }

    }

    /**
     * Tests that single character long non-numeric node labels can be parsed.
     */
    @Test
    public void testNodesAsCharacters() {

        // Arrange
        String text = "\ta\t[Weight=1];\n" +
                "\tb\t[Weight=2];\n" +
                "\ta\t->\tb\t[Weight=3];";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        //Act
        GraphReader reader = new DotGraphReader(stream);
        Graph graph = reader.read();

        //Assert
        Node firstNode = graph.getEntryPoints().get(0);
        Edge firstNodeFirstEdge = graph.getOutgoingEdges(firstNode).get(0);
        Node secondNode = firstNodeFirstEdge.getDestinationNode();

        assertEquals("a", firstNode.getLabel());
        assertEquals(1, firstNode.getComputationCost());
        assertEquals("b", secondNode.getLabel());
        assertEquals(2, secondNode.getComputationCost());
        assertEquals(firstNode, firstNodeFirstEdge.getOriginNode());
        assertEquals(secondNode, firstNodeFirstEdge.getDestinationNode());
        assertEquals(3, firstNodeFirstEdge.getCost());

    }

    /**
     * This tests for the ability to have node labels as multi-character strings
     */
    @Test
    public void testNodesAsMultiCharacterStrings() {

        // Arrange
        String text = "\tcheersHowsYaMum\t[Weight=1];\n" +
                "\tbethOhShesGood\t[Weight=2];\n" +
                "\tcheersHowsYaMum\t->\tbethOhShesGood\t[Weight=3];";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        //Act
        GraphReader reader = new DotGraphReader(stream);
        Graph graph = reader.read();

        //Assert
        Node firstNode = graph.getEntryPoints().get(0);
        Edge firstNodeFirstEdge = graph.getOutgoingEdges(firstNode).get(0);
        Node secondNode = firstNodeFirstEdge.getDestinationNode();

        assertEquals("cheersHowsYaMum", firstNode.getLabel());
        assertEquals(1, firstNode.getComputationCost());
        assertEquals("bethOhShesGood", secondNode.getLabel());
        assertEquals(2, secondNode.getComputationCost());
        assertEquals(firstNode, firstNodeFirstEdge.getOriginNode());
        assertEquals(secondNode, firstNodeFirstEdge.getDestinationNode());
        assertEquals(3, firstNodeFirstEdge.getCost());
    }

    /**
     * Tests that reading works properly with an edge beginning the file
     */
    @Test
    public void testTextStartingWithEdge() {

        // Arrange
        String text = "\t0\t->\t1\t[Weight=3];" +
                "\t0\t[Weight=1];\n" +
                "\t1\t[Weight=2];\n";
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

        //Act
        GraphReader reader = new DotGraphReader(stream);
        Graph graph = reader.read();

        //Assert
        Node firstNode = graph.getEntryPoints().get(0);
        Edge firstNodeFirstEdge = graph.getOutgoingEdges(firstNode).get(0);
        Node secondNode = firstNodeFirstEdge.getDestinationNode();

        assertEquals("0", firstNode.getLabel());
        assertEquals(1, firstNode.getComputationCost());
        assertEquals("1", secondNode.getLabel());
        assertEquals(2, secondNode.getComputationCost());
        assertEquals(firstNode, firstNodeFirstEdge.getOriginNode());
        assertEquals(secondNode, firstNodeFirstEdge.getDestinationNode());
        assertEquals(3, firstNodeFirstEdge.getCost());
    }

}