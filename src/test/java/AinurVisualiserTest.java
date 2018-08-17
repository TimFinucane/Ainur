import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("gandalf")
public class AinurVisualiserTest {

    @Test
    public void testVisualiser() {
        Ainur.main(new String[]{"data/graphs/Nodes_11_OutTree.dot", "4", "-v"});
    }

}
