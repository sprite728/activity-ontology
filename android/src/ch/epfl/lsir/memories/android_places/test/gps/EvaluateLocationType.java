package ch.epfl.lsir.memories.android_places.test.gps;

import ch.epfl.lsir.memories.android_places.utils.LocationUtils;
import ch.epfl.lsir.memories.android_places.utils.Utils;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Evaluate the Places API against provided data.
 *
 * @author Sebastian Claici
 */
public final class EvaluateLocationType {

    private EvaluateLocationType() {
    }

    /**
     * Read in a file of GPS coordinates and extract the types from each coordinate.
     *
     * The files are taken from the Microsoft Geolife dataset.
     *
     * @param infile File to read data from.
     * @param outfile File to write results to.
     */
    public static void evaluateAgainstFile(File infile, File outfile) throws IOException, ParseException {
        LocationUtils.initLocationTypes(new BufferedReader(
                new FileReader("/home/sebastian/Documents/Internship/Ontology/Places API/res/raw/locations.txt")));

        BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
        BufferedReader in = new BufferedReader(new FileReader(infile));

        // Discard first 6 lines;
        for (int i = 0; i < 6; ++i, in.readLine()) ;

        // Read data, find location type, and write to output file
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            int firstComma = line.indexOf(",");
            String coords = line.substring(0, line.indexOf(",", firstComma + 1));

            String[] params = new String[2];
            params[0] = coords;
            params[1] = "100";

            String[] values = line.split(",");
            String type = LocationUtils.getLocationType(params);
            out.write(type);
            out.write("," + values[5] + "," + values[6]);
            out.newLine();
        }
        out.close();
    }

    /**
     * Recursively evaluate each file in a directory structure.
     *
     * I assume files that contain relevant data have the .plt extension.
     *
     * @param directory Directory to recursively explore.
     */
    public static void evaluateAgainstDirectory(File directory) {
        if (!directory.isDirectory())
            throw new IllegalArgumentException();

        for (File file : directory.listFiles()) {
            if (file.isFile() && Utils.getExtension(file).equals(".plt")) {
                System.out.println(file.getName());
                File outfile = new File(file.getAbsolutePath() + ".out");
                try {
                    evaluateAgainstFile(file, outfile);
                } catch (IOException ioe) {
                    System.out.println("Could not open file " + file.getName() + " for reading.");
                } catch (ParseException pe) {
                    System.out.println("File is not in the proper format.");
                }
                break;
            } else if (file.isDirectory()) {
                evaluateAgainstDirectory(file);
            }
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        File directory = new File("/home/sebastian/Documents/Internship/Geolife Trajectories 1.3/Data/000/");
        for (final File subdir : directory.listFiles()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    evaluateAgainstDirectory(subdir);
                }
            }).start();
        }
    }
}

