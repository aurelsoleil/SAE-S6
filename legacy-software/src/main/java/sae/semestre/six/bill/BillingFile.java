package sae.semestre.six.bill;

import java.io.File;
import java.io.FileWriter;

public class BillingFile {

    private static final String FILE_PATH = "billing_records.txt";

    private static final String DIRECTORY_PATH = "./legacy-software/bills/";

    private static FileWriter fileWriter;

    public static void write(String data) {
        if (fileWriter == null) {
            init();
        }

        try {
            fileWriter.write(data + "\n");
            fileWriter.flush();
        } catch (Exception e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

    }

    private static void init() {
        try {
            File directory = new File(DIRECTORY_PATH);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(DIRECTORY_PATH + FILE_PATH);

            if (!file.exists()) {
                file.createNewFile();
            }

            fileWriter = new FileWriter(file, true);

        } catch (Exception e) {
            System.err.println("Error initializing file writer: " + e.getMessage());
        }
    }
}
