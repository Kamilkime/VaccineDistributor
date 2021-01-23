package pl.edu.pw.ee.aisd.vaccinedistributor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.aisd.vaccinedistributor.data.DataSection;

public class DataSectionTester {

    @Test
    public void testLongerSectionTitle() {
        Assertions.assertEquals(DataSection.PHARMACIES, DataSection.getSection("# Apteki (jakis wiekszy tytul)"));
        Assertions.assertEquals(DataSection.PRODUCERS, DataSection.getSection("# Producenci szczepionek         "));
        Assertions.assertEquals(DataSection.CONNECTIONS, DataSection.getSection("# Połączenia producentów i aptek###"));
    }

    @Test
    public void testWrongSectionTitle() {
        Assertions.assertEquals(DataSection.NONE, DataSection.getSection("# Apteka"));
        Assertions.assertEquals(DataSection.NONE, DataSection.getSection("#Apteki"));
        Assertions.assertEquals(DataSection.NONE, DataSection.getSection("# Producenci"));
        Assertions.assertEquals(DataSection.NONE, DataSection.getSection("# Połączenia producentów"));
        Assertions.assertEquals(DataSection.NONE, DataSection.getSection("# Cokolwiek"));
    }

}
