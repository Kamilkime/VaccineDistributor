package pl.edu.pw.ee.aisd.vaccinedistributor.pharmacy;

import pl.edu.pw.ee.aisd.vaccinedistributor.connection.Connection;
import pl.edu.pw.ee.aisd.vaccinedistributor.data.file.DataLoader;
import pl.edu.pw.ee.aisd.vaccinedistributor.producer.Producer;

import java.util.*;

public final class Pharmacy {

    private final int id;
    private final String name;
    private final int demand;

    private final Map<Connection, Integer> vaccinesBought;
    private final Map<Producer, Connection> connections;
    private final Map<Producer, Boolean> preferredProducers;
    private int supplyLeniency;

    private Pharmacy(final int id, final String name, final int demand) {
        this.id = id;
        this.name = name;
        this.demand = demand;

        this.vaccinesBought = new HashMap<>();
        this.connections = new HashMap<>();
        this.preferredProducers = new LinkedHashMap<>();
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getDemand() {
        return this.demand;
    }

    public int getSupplyLeniency() {
        return this.supplyLeniency;
    }

    public Map<Connection, Integer> getVaccinesBought() {
        return this.vaccinesBought;
    }

    public int getTotalVaccinesBought() {
        return this.vaccinesBought.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void buyVaccines(final Connection connection, final int vaccines) {
        this.preferredProducers.remove(connection.getProducer());

        if (vaccines != 0) {
            this.vaccinesBought.put(connection, vaccines);
        }
    }

    public void addConnection(final Connection connection) {
        this.connections.put(connection.getProducer(), connection);
    }

    public void createPreferences() {
        final List<Producer> bestProducers = new ArrayList<>();
        double lowestPrice = Double.MAX_VALUE;

        int potentialSupply = 0;
        for (final Connection connection : this.connections.values()) {
            potentialSupply += connection.getTransportLimit();
        }

        while (!this.connections.isEmpty()) {
            for (final Connection connection : this.connections.values()) {
                final double price = connection.getCost();
                if (lowestPrice < price) {
                    continue;
                }

                if (lowestPrice > price) {
                    lowestPrice = price;
                    bestProducers.clear();
                }

                bestProducers.add(connection.getProducer());
            }

            if (bestProducers.size() > 1) {
                bestProducers.sort(Comparator.comparingInt(p -> Pharmacy.this.connections.get(p).getTransportLimit()).reversed());
            }

            for (final Producer producer : bestProducers) {
                final Connection connection = this.connections.get(producer);
                final boolean needed = potentialSupply - connection.getTransportLimit() < this.demand;

                this.preferredProducers.put(producer, needed);
                this.connections.remove(producer);
            }

            bestProducers.clear();
            lowestPrice = Double.MAX_VALUE;
        }

        this.supplyLeniency = potentialSupply - this.demand;
    }

    public int getProducerPreference(final Producer producer) {
        return new ArrayList<>(this.preferredProducers.keySet()).indexOf(producer);
    }

    public boolean getProducerNecessity(final Producer producer) {
        return this.preferredProducers.getOrDefault(producer, false);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Pharmacy pharmacy = (Pharmacy) o;
        return this.id == pharmacy.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public static Pharmacy fromString(final String line) throws IllegalArgumentException {
        final String[] lineSplit = DataLoader.LINE_SPLIT_PATTERN.split(line);

        if (lineSplit.length != 3) {
            System.out.println("Błędna linia: \"" + line + "\"");
            System.out.println("Powód - zła liczba danych");

            throw new IllegalArgumentException();
        }

        final int id;
        try {
            id = Integer.parseInt(lineSplit[0]);
        } catch (final NumberFormatException exception) {
            System.out.println("Błędna linia: \"" + line + "\"");
            System.out.println("Powód - pierwszy parametr musi być liczbą całkowitą");

            throw new IllegalArgumentException();
        }

        final int demand;
        try {
            demand = Integer.parseInt(lineSplit[2]);

            if (demand <= 0) {
                throw new NumberFormatException();
            }
        } catch (final NumberFormatException exception) {
            System.out.println("Błędna linia: \"" + line + "\"");
            System.out.println("Powód - trzeci parametr musi być liczbą całkowitą, większą od 0");

            throw new IllegalArgumentException();
        }

        return new Pharmacy(id, lineSplit[1], demand);
    }


}
