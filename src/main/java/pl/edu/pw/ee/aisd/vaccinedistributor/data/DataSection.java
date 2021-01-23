package pl.edu.pw.ee.aisd.vaccinedistributor.data;

public enum DataSection {

    PRODUCERS("# Producenci szczepionek", "Producenci"),
    PHARMACIES("# Apteki", "Apteki"),
    CONNECTIONS("# Połączenia producentów i aptek", "Połączenia"),
    NONE("", "");

    private final String sectionHeader;
    private final String sectionName;

    DataSection(final String sectionHeader, final String sectionName) {
        this.sectionHeader = sectionHeader;
        this.sectionName = sectionName;
    }

    public String getSectionName() {
        return this.sectionName;
    }

    public static DataSection getSection(final String sectionHeader) {
        if (sectionHeader.startsWith(PRODUCERS.sectionHeader)) {
            return PRODUCERS;
        }

        if (sectionHeader.startsWith(PHARMACIES.sectionHeader)) {
            return PHARMACIES;
        }

        if (sectionHeader.startsWith(CONNECTIONS.sectionHeader)) {
            return CONNECTIONS;
        }

        return NONE;
    }

}
