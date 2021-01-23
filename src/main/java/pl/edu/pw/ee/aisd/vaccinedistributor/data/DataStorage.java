package pl.edu.pw.ee.aisd.vaccinedistributor.data;

import pl.edu.pw.ee.aisd.vaccinedistributor.connection.Connection;
import pl.edu.pw.ee.aisd.vaccinedistributor.data.file.DataLoader;
import pl.edu.pw.ee.aisd.vaccinedistributor.pharmacy.Pharmacy;
import pl.edu.pw.ee.aisd.vaccinedistributor.producer.Producer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class DataStorage {

    public Collection<Producer> producers;
    public Collection<Pharmacy> pharmacies;
    public Collection<Connection> connections;

    public boolean loadData(final File inFile) throws FileNotFoundException {
        final DataLoader dataLoader = new DataLoader(inFile);
        if (!dataLoader.loadLines()) {
            return false;
        }

        final Map<Integer, Producer> producers = dataLoader.loadProducers();
        if (producers.isEmpty()) {
            return false;
        }

        final Map<Integer, Pharmacy> pharmacies = dataLoader.loadPharmacies();
        if (pharmacies.isEmpty()) {
            return false;
        }

        final Set<Connection> connections = dataLoader.loadConnections(producers, pharmacies);
        if (connections.isEmpty()) {
            return false;
        }

        this.producers = producers.values();
        this.pharmacies = pharmacies.values();
        this.connections = connections;

        return true;
    }

    public Connection getConnection(final Producer producer, final Pharmacy pharmacy) {
        for (final Connection connection : this.connections) {
            if (!connection.getProducer().equals(producer)) {
                continue;
            }

            if (!connection.getPharmacy().equals(pharmacy)) {
                continue;
            }

            return connection;
        }

        return null;
    }

}
