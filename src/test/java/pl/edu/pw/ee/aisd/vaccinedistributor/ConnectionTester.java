package pl.edu.pw.ee.aisd.vaccinedistributor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.edu.pw.ee.aisd.vaccinedistributor.connection.Connection;
import pl.edu.pw.ee.aisd.vaccinedistributor.pharmacy.Pharmacy;
import pl.edu.pw.ee.aisd.vaccinedistributor.producer.Producer;

import java.util.HashMap;
import java.util.Map;

public class ConnectionTester {

    @Test
    public void testParsingFromString() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Connection.fromString("0 | 0 | 800", new HashMap<>(), new HashMap<>());
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Connection.fromString("0.1 | 0 | 800 | 70.5", new HashMap<>(), new HashMap<>());
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Connection.fromString("0 | s | 800 | 70.5", new HashMap<>(), new HashMap<>());
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Connection.fromString("0 | 0 | -1 | 70.5", new HashMap<>(), new HashMap<>());
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Connection.fromString("0 | 0 | 1.1 | 70.5", new HashMap<>(), new HashMap<>());
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Connection.fromString("0 | 0 | 1 | -1", new HashMap<>(), new HashMap<>());
        });

        Assertions.assertDoesNotThrow(() -> {
            Connection.fromString("0 | 0 | 1 | 0", new HashMap<>(), new HashMap<>());
        });
    }

    @Test
    public void testEquality() {
        final Map<Integer, Producer> producers = new HashMap<>();
        final Map<Integer, Pharmacy> pharmacies = new HashMap<>();

        producers.put(0, Producer.fromString("0 | BioTech 2.0 | 900"));
        producers.put(1, Producer.fromString("1 | Eko Polska 2020 | 1300"));
        pharmacies.put(0, Pharmacy.fromString("0 | CentMedEko Centrala | 450"));
        pharmacies.put(1, Pharmacy.fromString("1 | CentMedEko 24h | 690"));

        final Connection connection1 = Connection.fromString("0 | 0 | 800 | 70.5", producers, pharmacies);
        final Connection connection2 = Connection.fromString("0 | 0 | 700 | 70.5", producers, pharmacies);
        final Connection connection3 = Connection.fromString("0 | 1 | 800 | 70.5", producers, pharmacies);
        final Connection connection4 = Connection.fromString("1 | 0 | 800 | 70.5", producers, pharmacies);
        final Connection connection5 = Connection.fromString("1 | 1 | 800 | 70.5", producers, pharmacies);

        Assertions.assertEquals(connection1, connection2);
        Assertions.assertNotEquals(connection1, connection3);
        Assertions.assertNotEquals(connection2, connection4);
        Assertions.assertNotEquals(connection1, connection5);
        Assertions.assertNotEquals(connection3, connection4);
        Assertions.assertNotEquals(connection3, connection5);
        Assertions.assertNotEquals(connection4, connection5);
    }

}
