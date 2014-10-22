package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.classifier.CategoryHierarchy;
import io.mem0r1es.activitysubsumer.utils.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Reading the Foursquare categories hierarchy from a csv file.
 *
 * @author Ivan Gavrilović
 */
public class FoursquareCategoriesCSV implements CategoriesProvider {

    private InputStream input;

    /**
     * Create new {@link io.mem0r1es.activitysubsumer.io.FoursquareCategoriesCSV}
     * @param input csv input stream to read in order to obtain categories tree
     */
    public FoursquareCategoriesCSV(InputStream input) {
        this.input = input;
    }

    @Override
    public Set<CategoryHierarchy.Category> read() {
        BufferedReader reader = null;
        Map<String, CategoryHierarchy.Category> categoryMap = new HashMap<String, CategoryHierarchy.Category>();
        try {
            reader = new BufferedReader(new InputStreamReader(input));
            String line = reader.readLine();
            while (line != null) {
                String parts[] = Utils.decodeParts(line);
                if (parts.length == 1) categoryMap.put(parts[0], new CategoryHierarchy.Category(parts[0]));
                else if (parts.length == 2) {
                    CategoryHierarchy.Category fst = categoryMap.get(parts[0]);
                    if (fst == null) fst = new CategoryHierarchy.Category(parts[0]);

                    CategoryHierarchy.Category snd = categoryMap.get(parts[1]);
                    if (snd == null) snd = new CategoryHierarchy.Category(parts[1]);

                    fst.addChild(snd);
                    snd.addParent(fst);
                    categoryMap.put(fst.getName(), fst);
                    categoryMap.put(snd.getName(), snd);
                }

                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new HashSet<CategoryHierarchy.Category>(categoryMap.values());
    }
}
