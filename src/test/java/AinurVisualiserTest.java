import common.categories.GandalfIntegrationTestsCategory;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(GandalfIntegrationTestsCategory.class)
public class AinurVisualiserTest {

    @Test
    public void testVisualiser() {
        Ainur.main(new String[]{"data/graphs/Nodes_11_OutTree.dot", "100", "-v"});
    }

}
