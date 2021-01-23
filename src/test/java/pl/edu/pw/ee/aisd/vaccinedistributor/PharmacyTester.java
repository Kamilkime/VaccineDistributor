package pl.edu.pw.ee.aisd.vaccinedistributor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.aisd.vaccinedistributor.pharmacy.Pharmacy;

public class PharmacyTester {

    @Test
    public void testParsingFromString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Pharmacy.fromString("0 | BioTech 2.0");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Pharmacy.fromString("0 |BioTech 2.0 | 100");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Pharmacy.fromString("1.1 | BioTech 2.0 | 100");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Pharmacy.fromString("0 | BioTech 2.0 | -100");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Pharmacy.fromString("0 | BioTech 2.0 | 100.1");
        });
    }

    @Test
    public void testEquality() {
        final Pharmacy pharmacy1 = Pharmacy.fromString("0 | BioTech 2.0 | 100");
        final Pharmacy pharmacy2 = Pharmacy.fromString("0 | BioTech 3.0 | 200");
        final Pharmacy pharmacy3 = Pharmacy.fromString("1 | BioTech 2.0 | 100");

        Assertions.assertEquals(pharmacy1, pharmacy2);
        Assertions.assertNotEquals(pharmacy1, pharmacy3);
    }

}
