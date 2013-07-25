package com.example.Places_API.test.gps;

import android.content.Context;
import com.example.Places_API.utils.Utils;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * @author Sebastian Claici
 */
public final class EvaluateLocationType {

    private EvaluateLocationType() {}

    public static void evaluateAgainstFile(File infile, File outfile) throws IOException, ParseException {
        Utils.initLocationTypes(new BufferedReader(new FileReader("/home/sebastian/Documents/Internship/Ontology/Places API/res/raw/locations.txt")));

        BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
        BufferedReader in = new BufferedReader(new FileReader(infile));

        // Discard first 6 lines;
        for (int i = 0; i < 6; ++i, in.readLine());

        // Read data, find location type, and write to output file
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            int firstComma = line.indexOf(",");
            String coords = line.substring(0, line.indexOf(",", firstComma + 1));

            String[] params = new String[2];
            params[0] = coords;
            params[1] = "100";

            String type = Utils.getLocationType(params);
            out.write(type);
            out.newLine();
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        File infile = new File("example infile");
        File outfile = new File("example outfile");

        evaluateAgainstFile(infile, outfile);
    }
}

