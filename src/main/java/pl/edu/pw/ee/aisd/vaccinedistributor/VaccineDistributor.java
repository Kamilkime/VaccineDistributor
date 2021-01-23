package pl.edu.pw.ee.aisd.vaccinedistributor;

import pl.edu.pw.ee.aisd.vaccinedistributor.data.DataStorage;
import pl.edu.pw.ee.aisd.vaccinedistributor.data.file.DataPrinter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class VaccineDistributor {

    public static void main(final String[] args) {
        if (args.length < 2) {
            System.out.println("Zbyt mała liczba argumentów");
            return;
        }

        final File inFile = new File(args[0]);
        if (!inFile.exists() || inFile.isDirectory()) {
            System.out.println("Brak pliku z danymi");
            return;
        }

        final File outFile = new File(args[1]);
        if (outFile.exists()) {
            System.out.println("Plik z wynikami już istnieje");
            return;
        }

        System.out.println("Wczytywanie danych...");
        final DataStorage dataStorage = new DataStorage();

        try {
            if (!dataStorage.loadData(inFile)) {
                return;
            }
        } catch (final FileNotFoundException exception) {
            System.out.println("Błąd krytyczny programu:");
            exception.printStackTrace();

            return;
        }

        System.out.println("Dane wczytane, przetwarzanie...");
        final DistributionCalculator calculator = new DistributionCalculator(dataStorage);
        calculator.calculate();

        System.out.println("Dane przetworzone, drukowanie wyników...");
        final DataPrinter dataPrinter = new DataPrinter(outFile);

        try {
            dataPrinter.printData(dataStorage);
        } catch (final IOException exception) {
            System.out.println("Błąd krytyczny programu:");
            exception.printStackTrace();
        }
    }

    private VaccineDistributor() {}

}
