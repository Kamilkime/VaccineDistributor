package pl.edu.pw.ee.aisd.vaccinedistributor.data.file;

import pl.edu.pw.ee.aisd.vaccinedistributor.connection.Connection;
import pl.edu.pw.ee.aisd.vaccinedistributor.data.DataStorage;
import pl.edu.pw.ee.aisd.vaccinedistributor.pharmacy.Pharmacy;
import pl.edu.pw.ee.aisd.vaccinedistributor.producer.Producer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class DataPrinter {

    private final File outFile;

    public DataPrinter(final File outFile) {
        this.outFile = outFile;
    }

    public void printData(final DataStorage dataStorage) throws IOException {
        final FileWriter fileWriter = new FileWriter(this.outFile);

        final Map<Pharmacy, Integer> shortages = new TreeMap<>(Comparator.comparingInt(Pharmacy::getID));
        final Map<Producer, Integer> overproductions = new TreeMap<>(Comparator.comparingInt(Producer::getID));
        final Map<Connection, Integer> transportedVaccines = new HashMap<>();

        for (final Pharmacy pharmacy : dataStorage.pharmacies) {
            final int totalBought = pharmacy.getTotalVaccinesBought();
            final int demand = pharmacy.getDemand();

            if (totalBought < demand) {
                shortages.put(pharmacy, demand - totalBought);
            }

            transportedVaccines.putAll(pharmacy.getVaccinesBought());
        }

        for (final Producer producer : dataStorage.producers) {
            final int totalSold = producer.getVaccinesSold();
            final int production = producer.getProductionLimit();

            if (totalSold < production) {
                overproductions.put(producer, production - totalSold);
            }
        }

        if (!overproductions.isEmpty()) {
            fileWriter.append("# Nadprodukcja\n");

            for (final Map.Entry<Producer, Integer> overproductionEntry : overproductions.entrySet()) {
                final Producer producer = overproductionEntry.getKey();
                final int overproduction = overproductionEntry.getValue();

                fileWriter.append(producer.getName()).append(" -> nadprodukcja ").append(Integer.toString(overproduction))
                        .append(" szt. przy ").append(Integer.toString(producer.getProductionLimit())).append(" szt. łącznej produkcji\n");
            }

            fileWriter.append("\n");
        }

        if (!shortages.isEmpty()) {
            fileWriter.append("# Braki\n");

            for (final Map.Entry<Pharmacy, Integer> shortageEntry : shortages.entrySet()) {
                final Pharmacy pharmacy = shortageEntry.getKey();
                final int shortage = shortageEntry.getValue();

                fileWriter.append(pharmacy.getName()).append(" -> brak ").append(Integer.toString(shortage)).append(" szt. z ")
                        .append(Integer.toString(pharmacy.getDemand())).append(" szt. zapotrzebowania\n");
            }

            fileWriter.append("\n");
        }

        fileWriter.append("# Połączenia\n");

        double totalCost = 0.0D;
        for (final Map.Entry<Connection, Integer> connectionEntry : transportedVaccines.entrySet()) {
            final Connection connection = connectionEntry.getKey();
            final Integer vaccines = connectionEntry.getValue();
            final double cost = Math.round(connection.getCost() * vaccines * 100.0D) / 100.0D;

            fileWriter.append(connection.getProducer().getName()).append(" -> ").append(connection.getPharmacy().getName())
                    .append(" [Koszt = ").append(Integer.toString(vaccines)).append(" * ").append(Double.toString(connection.getCost()))
                    .append(" = ").append(Double.toString(cost)).append(" zł]\n");

            totalCost += cost;
        }

        fileWriter.append("\nKoszt całkowity: ").append(Double.toString(totalCost)).append(" zł");
        fileWriter.close();
    }

}
