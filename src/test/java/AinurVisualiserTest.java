import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("gandalf")
public class AinurVisualiserTest {

    @Test
    public void testVisualiser() {
        Ainur.main(new String[]{"data/SampleData/Input/4p_Random_Nodes_30_Density_5.17_CCR_2.01_WeightType_Random.dot", "40", "-v", "-p", "4"});
    }

}
