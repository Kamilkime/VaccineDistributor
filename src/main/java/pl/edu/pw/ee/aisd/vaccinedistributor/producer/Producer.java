package pl.edu.pw.ee.aisd.vaccinedistributor.producer;

import pl.edu.pw.ee.aisd.vaccinedistributor.connection.Connection;
import pl.edu.pw.ee.aisd.vaccinedistributor.data.DataStorage;
import pl.edu.pw.ee.aisd.vaccinedistributor.data.file.DataLoader;
import pl.edu.pw.ee.aisd.vaccinedistributor.pharmacy.Pharmacy;

import java.util.*;

public final class Producer {

    private final int id;
    private final String name;
    private final int productionLimit;

    private final Map<Pharmacy, Map.Entry<Integer, Boolean>> pharmacyPreferences;
    private final List<Pharmacy> preferredPharmacies;
    private int vaccinesSold;

    private Producer(final int id, final String name, final int productionLimit) {
        this.id = id;
        this.name = name;
        this.productionLimit = productionLimit;

        this.pharmacyPreferences = new HashMap<>();
        this.preferredPharmacies = new ArrayList<>();
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getProductionLimit() {
        return this.productionLimit;
    }

    public int getVaccinesSold() {
        return this.vaccinesSold;
    }

    public void sellVaccines(final Connection connection, final int vaccines) {
        this.preferredPharmacies.remove(connection.getPharmacy());

        if (vaccines != 0) {
            this.vaccinesSold += vaccines;
        }
    }

    public void addPharmacy(final Pharmacy pharmacy, final int preference, final boolean necessity) {
        this.pharmacyPreferences.put(pharmacy, new AbstractMap.SimpleImmutableEntry<>(preference, necessity));
    }

    public void createPreferences(final DataStorage dataStorage) {
        final List<Pharmacy> bestPharmacies = new ArrayList<>();
        double bestPreference = Double.MAX_VALUE;
        boolean necessityFirst = false;

        while (!this.pharmacyPreferences.isEmpty()) {
            for (final Map.Entry<Pharmacy, Map.Entry<Integer, Boolean>> preferenceEntry : this.pharmacyPreferences.entrySet()) {
                final boolean necessity = preferenceEntry.getValue().getValue();
                if (necessity || necessityFirst) {
                    if (!necessityFirst) {
                        necessityFirst = true;
                        bestPharmacies.clear();
                    }

                    if (necessity) {
                        bestPharmacies.add(preferenceEntry.getKey());
                    }

                    continue;
                }

                final int preference = preferenceEntry.getValue().getKey();
                if (bestPreference < preference) {
                    continue;
                }

                if (bestPreference > preference) {
                    bestPreference = preference;
                    bestPharmacies.clear();
                }

                bestPharmacies.add(preferenceEntry.getKey());
            }

            final boolean sortWithNecessity = necessityFirst;
            if (bestPharmacies.size() > 1) {
                bestPharmacies.sort((p1, p2) -> {
                    if (sortWithNecessity) {
                        return Integer.compare(p1.getSupplyLeniency(), p2.getSupplyLeniency());
                    }

                    final int p1Limit = dataStorage.getConnection(Producer.this, p1).getTransportLimit();
                    final int p2Limit = dataStorage.getConnection(Producer.this, p2).getTransportLimit();

                    return Double.compare(1.0D * p2Limit / p2.getDemand(), 1.0D * p1Limit / p1.getDemand());
                });
            }

            for (final Pharmacy pharmacy : bestPharmacies) {
                this.preferredPharmacies.add(pharmacy);
                this.pharmacyPreferences.remove(pharmacy);
            }

            bestPharmacies.clear();
            bestPreference = Double.MAX_VALUE;
            necessityFirst = false;
        }
    }

    public int getPharmacyPreference(final Pharmacy pharmacy) {
        return this.preferredPharmacies.indexOf(pharmacy);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Producer producer = (Producer) o;
        return this.id == producer.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public static Producer fromString(final String line) throws IllegalArgumentException {
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

        final int productionLimit;
        try {
            productionLimit = Integer.parseInt(lineSplit[2]);

            if (productionLimit <= 0) {
                throw new NumberFormatException();
            }
        } catch (final NumberFormatException exception) {
            System.out.println("Błędna linia: \"" + line + "\"");
            System.out.println("Powód - trzeci parametr musi być liczbą całkowitą, większą od 0");

            throw new IllegalArgumentException();
        }

        return new Producer(id, lineSplit[1], productionLimit);
    }

}
