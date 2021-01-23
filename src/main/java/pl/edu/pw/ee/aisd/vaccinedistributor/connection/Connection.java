package pl.edu.pw.ee.aisd.vaccinedistributor.connection;

import pl.edu.pw.ee.aisd.vaccinedistributor.data.file.DataLoader;
import pl.edu.pw.ee.aisd.vaccinedistributor.pharmacy.Pharmacy;
import pl.edu.pw.ee.aisd.vaccinedistributor.producer.Producer;

import java.util.Map;
import java.util.Objects;

public final class Connection {

    private final Producer producer;
    private final Pharmacy pharmacy;
    private final int transportLimit;
    private final double cost;

    private boolean used;

    private Connection(final Producer producer, final Pharmacy pharmacy, final int transportLimit, final double cost) {
        this.producer = producer;
        this.pharmacy = pharmacy;
        this.transportLimit = transportLimit;
        this.cost = cost;
    }

    public Producer getProducer() {
        return this.producer;
    }

    public Pharmacy getPharmacy() {
        return this.pharmacy;
    }

    public int getTransportLimit() {
        return this.transportLimit;
    }

    public double getCost() {
        return this.cost;
    }

    public boolean wasUsed() {
        return this.used;
    }

    public void setUsed(final boolean used, final int vaccines) {
        this.used = used;

        this.producer.sellVaccines(this, vaccines);
        this.pharmacy.buyVaccines(this, vaccines);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Connection that = (Connection) o;
        return this.producer.equals(that.producer) && this.pharmacy.equals(that.pharmacy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.producer, this.pharmacy);
    }

    public static Connection fromString(final String line, final Map<Integer, Producer> producers, final Map<Integer, Pharmacy> pharmacies)
            throws IllegalArgumentException {
        final String[] lineSplit = DataLoader.LINE_SPLIT_PATTERN.split(line);

        if (lineSplit.length != 4) {
            System.out.println("Błędna linia: \"" + line + "\"");
            System.out.println("Powód - zła liczba danych");

            throw new IllegalArgumentException();
        }

        final int producerID;
        try {
            producerID = Integer.parseInt(lineSplit[0]);
        } catch (final NumberFormatException exception) {
            System.out.println("Błędna linia: \"" + line + "\"");
            System.out.println("Powód - pierwszy parametr musi być liczbą całkowitą");

            throw new IllegalArgumentException();
        }

        final int pharmacyID;
        try {
            pharmacyID = Integer.parseInt(lineSplit[1]);
        } catch (final NumberFormatException exception) {
            System.out.println("Błędna linia: \"" + line + "\"");
            System.out.println("Powód - drugi parametr musi być liczbą całkowitą");

            throw new IllegalArgumentException();
        }

        final int transportLimit;
        try {
            transportLimit = Integer.parseInt(lineSplit[2]);

            if (transportLimit <= 0) {
                throw new NumberFormatException();
            }
        } catch (final NumberFormatException exception) {
            System.out.println("Błędna linia: \"" + line + "\"");
            System.out.println("Powód - trzeci parametr musi być liczbą całkowitą, większą od 0");

            throw new IllegalArgumentException();
        }

        final double cost;
        try {
            cost = Double.parseDouble(lineSplit[3]);

            if (cost < 0) {
                throw new NumberFormatException();
            }
        } catch (final NumberFormatException exception) {
            System.out.println("Błędna linia: \"" + line + "\"");
            System.out.println("Powód - czwarty parametr musi być liczbą rzeczywistą, nieujemną");

            throw new IllegalArgumentException();
        }

        return new Connection(producers.get(producerID), pharmacies.get(pharmacyID), transportLimit, cost);
    }

}
