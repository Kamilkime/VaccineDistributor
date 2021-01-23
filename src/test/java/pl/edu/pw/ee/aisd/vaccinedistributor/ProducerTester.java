package pl.edu.pw.ee.aisd.vaccinedistributor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.aisd.vaccinedistributor.pharmacy.Pharmacy;
import pl.edu.pw.ee.aisd.vaccinedistributor.producer.Producer;

public class ProducerTester {

    @Test
    public void testParsingFromString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Pharmacy.fromString("0 | CentMedEko Centrala");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Pharmacy.fromString("0 |CentMedEko Centrala | 100");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Pharmacy.fromString("1.1 | CentMedEko Centrala | 100");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Pharmacy.fromString("0 | CentMedEko Centrala | -100");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Pharmacy.fromString("0 | CentMedEko Centrala | 100.1");
        });
    }

    @Test
    public void testEquality() {
        final Producer producer1 = Producer.fromString("1 | CentMedEko Centrala | 100");
        final Producer producer2 = Producer.fromString("1 | CentMedEko | 300");
        final Producer producer3 = Producer.fromString("2 | CentMedEko Centrala | 100");

        Assertions.assertEquals(producer1, producer2);
        Assertions.assertNotEquals(producer1, producer3);
    }

}
