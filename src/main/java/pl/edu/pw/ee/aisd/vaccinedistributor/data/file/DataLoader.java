package pl.edu.pw.ee.aisd.vaccinedistributor.data.file;

import pl.edu.pw.ee.aisd.vaccinedistributor.connection.Connection;
import pl.edu.pw.ee.aisd.vaccinedistributor.data.DataSection;
import pl.edu.pw.ee.aisd.vaccinedistributor.pharmacy.Pharmacy;
import pl.edu.pw.ee.aisd.vaccinedistributor.producer.Producer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Pattern;

public final class DataLoader {

    private final File inFile;
    private final Map<DataSection, Set<String>> dataLines = new EnumMap<>(DataSection.class);

    public static final Pattern LINE_SPLIT_PATTERN = Pattern.compile("\\s+\\|\\s+");

    public DataLoader(final File inFile) {
        this.inFile = inFile;
    }

    public boolean loadLines() throws FileNotFoundException {
        final Scanner fileScanner = new Scanner(this.inFile);

        DataSection currentSection = DataSection.NONE;
        while (fileScanner.hasNextLine()) {
            final String line = fileScanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("#")) {
                currentSection = DataSection.getSection(line);

                if (this.dataLines.containsKey(currentSection)) {
                    System.out.println("Powtórzenie sekcji \"" + currentSection.getSectionName() + "\"");
                    return false;
                }

                if (currentSection != DataSection.NONE) {
                    this.dataLines.put(currentSection, new HashSet<>());
                }

                continue;
            }

            if (currentSection == DataSection.NONE) {
                continue;
            }

            this.dataLines.get(currentSection).add(line);
        }

        fileScanner.close();
        return true;
    }

    public Map<Integer, Producer> loadProducers() {
        final Set<String> lines = this.dataLines.get(DataSection.PRODUCERS);
        final Map<Integer, Producer> producers = new HashMap<>();

        if (lines == null || lines.isEmpty()) {
            System.out.println("Brak sekcji \"" + DataSection.PRODUCERS.getSectionName() + "\"");
            return producers;
        }

        for (final String line : lines) {
            try {
                final Producer producer = Producer.fromString(line);

                if (producers.containsKey(producer.getID())) {
                    System.out.println("Błędna linia: \"" + line + "\"");
                    System.out.println("Powód - powtórzone ID producenta");

                    throw new IllegalArgumentException();
                }

                producers.put(producer.getID(), producer);
            } catch (final IllegalArgumentException exception) {
                return new HashMap<>();
            }
        }

        return producers;
    }

    public Map<Integer, Pharmacy> loadPharmacies() {
        final Set<String> lines = this.dataLines.get(DataSection.PHARMACIES);
        final Map<Integer, Pharmacy> pharmacies = new HashMap<>();

        if (lines == null || lines.isEmpty()) {
            System.out.println("Brak sekcji \"" + DataSection.PHARMACIES.getSectionName() + "\"");
            return pharmacies;
        }

        for (final String line : lines) {
            try {
                final Pharmacy pharmacy = Pharmacy.fromString(line);

                if (pharmacies.containsKey(pharmacy.getID())) {
                    System.out.println("Błędna linia: \"" + line + "\"");
                    System.out.println("Powód - powtórzone ID apteki");

                    throw new IllegalArgumentException();
                }

                pharmacies.put(pharmacy.getID(), pharmacy);
            } catch (final IllegalArgumentException exception) {
                return new HashMap<>();
            }
        }

        return pharmacies;
    }

    public Set<Connection> loadConnections(final Map<Integer, Producer> producers, final Map<Integer, Pharmacy> pharmacies) {
        final Set<String> lines = this.dataLines.get(DataSection.CONNECTIONS);
        final Set<Connection> connections = new HashSet<>();

        if (lines == null || lines.isEmpty()) {
            System.out.println("Brak sekcji \"" + DataSection.CONNECTIONS.getSectionName() + "\"");
            return connections;
        }

        for (final String line : lines) {
            try {
                final Connection connection = Connection.fromString(line, producers, pharmacies);

                if (connection.getProducer() == null) {
                    System.out.println("Błędna linia: \"" + line + "\"");
                    System.out.println("Powód - brak producenta o takim ID");

                    throw new IllegalArgumentException();
                }

                if (connection.getPharmacy() == null) {
                    System.out.println("Błędna linia: \"" + line + "\"");
                    System.out.println("Powód - brak apteki o takim ID");

                    throw new IllegalArgumentException();
                }

                if (connection.getTransportLimit() > connection.getProducer().getProductionLimit()) {
                    System.out.println("Błędna linia: \"" + line + "\"");
                    System.out.println("Powód - połączenie nie może większej liczby szczepionek niż możliwości producenta");

                    throw new IllegalArgumentException();
                }

                for (final Connection loadedConnection : connections) {
                    if (!loadedConnection.equals(connection)) {
                        continue;
                    }

                    System.out.println("Błędna linia: \"" + line + "\"");
                    System.out.println("Powód - powtórzenie połączenia dla tego producenta i apteki");

                    throw new IllegalArgumentException();
                }

                connections.add(connection);
            } catch (final IllegalArgumentException exception) {
                return new HashSet<>();
            }
        }

        return connections;
    }

}
