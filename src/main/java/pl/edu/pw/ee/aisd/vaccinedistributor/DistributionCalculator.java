package pl.edu.pw.ee.aisd.vaccinedistributor;

import pl.edu.pw.ee.aisd.vaccinedistributor.connection.Connection;
import pl.edu.pw.ee.aisd.vaccinedistributor.data.DataStorage;
import pl.edu.pw.ee.aisd.vaccinedistributor.pharmacy.Pharmacy;
import pl.edu.pw.ee.aisd.vaccinedistributor.producer.Producer;

import java.util.HashMap;
import java.util.Map;

public class DistributionCalculator {

    private final DataStorage dataStorage;

    public DistributionCalculator(final DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    public void calculate() {
        this.preparePreferences();

        final Map<Connection, Integer> vaccinesBought = new HashMap<>();
        while (this.producersHaveCapacity() && this.pharmaciesHaveDemand() && this.connectionsAvailable()) {
            for (final Connection connection : this.dataStorage.connections) {
                if (connection.wasUsed()) {
                    continue;
                }

                final Producer producer = connection.getProducer();
                final Pharmacy pharmacy = connection.getPharmacy();

                if (producer.getPharmacyPreference(pharmacy) != 0) {
                    continue;
                }

                if (pharmacy.getProducerPreference(producer) != 0) {
                    continue;
                }

                final int capacity = producer.getProductionLimit() - producer.getVaccinesSold();
                final int demand = pharmacy.getDemand() - pharmacy.getTotalVaccinesBought();

                final int vaccines = Math.min(connection.getTransportLimit(), Math.min(capacity, demand));
                if (vaccines == 0) {
                    connection.setUsed(true, 0);
                    continue;
                }

                vaccinesBought.put(connection, vaccines);
            }

            for (final Map.Entry<Connection, Integer> purchase : vaccinesBought.entrySet()) {
                purchase.getKey().setUsed(true, purchase.getValue());
            }

            vaccinesBought.clear();
        }
    }

    private void preparePreferences() {
        for (final Pharmacy pharmacy : this.dataStorage.pharmacies) {
            for (final Connection connection : this.dataStorage.connections) {
                if (!connection.getPharmacy().equals(pharmacy)) {
                    continue;
                }

                pharmacy.addConnection(connection);
            }

            pharmacy.createPreferences();
        }

        for (final Producer producer : this.dataStorage.producers) {
            for (final Pharmacy pharmacy : this.dataStorage.pharmacies) {
                final int preference = pharmacy.getProducerPreference(producer);
                if (preference == -1) {
                    continue;
                }

                producer.addPharmacy(pharmacy, preference, pharmacy.getProducerNecessity(producer));
            }

            producer.createPreferences(this.dataStorage);
        }
    }

    private boolean producersHaveCapacity() {
        for (final Producer producer : this.dataStorage.producers) {
            if (producer.getVaccinesSold() != producer.getProductionLimit()) {
                return true;
            }
        }

        return false;
    }

    private boolean pharmaciesHaveDemand() {
        for (final Pharmacy pharmacy : this.dataStorage.pharmacies) {
            if (pharmacy.getTotalVaccinesBought() != pharmacy.getDemand()) {
                return true;
            }
        }

        return false;
    }

    private boolean connectionsAvailable() {
        for (final Connection connection : this.dataStorage.connections) {
            if (!connection.wasUsed()) {
                return true;
            }
        }

        return false;
    }

}
