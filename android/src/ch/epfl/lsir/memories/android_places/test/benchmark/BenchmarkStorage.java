package ch.epfl.lsir.memories.android_places.test.benchmark;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import ch.epfl.lsir.memories.android_places.utils.Utils;
import com.example.Places_API.R;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import deri.org.store.BDBGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Sebastian Claici
 */
public final class BenchmarkStorage {

    public static final boolean DEBUG = false;
    public static SQLiteOpenHelper db;

    public static void runTest(Context context) throws IOException {
        db = new MappingOpenHelper(context);

        Log.d("BENCHMARK", "=============================================================");
        //runStoreTest(context, 1000);
        runQueryTest(context, 1000);
    }

    /**
     * Run insert on storage and log results.
     *
     * @param context Context of the Android app.
     * @param N Number of runs for each query.
     */
    public static void runStoreTest(Context context, int N) throws IOException {
        int step = 10;
        for (int n = N; n <= N; n += step) {
            Log.d("BENCHMARK", "RDF Store (" + n + "): " + benchmarkRDFStore(context, n) + "ms");
            Log.d("BENCHMARK", "Regular Store (" + n + "): " + benchmarkRegularStore(context, n) + "ms");
            Log.d("BENCHMARK", "DB Store (" + n + "): " + benchmarkDBStore(context, n) + "ms");
        }
    }

    /**
     * Run queries on storage and log results.
     *
     * @param context Context of the Android app.
     * @param N Number of runs for each query.
     */
    public static void runQueryTest(Context context, int N) throws IOException {
        int step = 10;
        for (int n = 10; n <= N; n += step) {
            Log.d("BENCHMARK", "RDF Query (" + n + "): " + benchmarkRDFQuery(context, n) + "ms");
            Log.d("BENCHMARK", "Regular Query (" + n + "): " + benchmarkRegularQuery(context, n) + "ms");
            Log.d("BENCHMARK", "DB Query (" + n + "): " + benchmarkDBQuery(context, n) + "ms");
        }
    }

    /**
     * Benchmark querying on RDF On the Go
     *
     * @param context Context of the Android app.
     * @param n Number of queries to run.
     * @return Time in milliseconds to execute queries.
     */
    private static long benchmarkRDFQuery(Context context, int n) {
        String predicate = "http://example.org/hasLocation";

        BDBGraph graph = new BDBGraph("benchmark2");
        ModelCom model = new ModelCom(graph);

        String queryString = Utils.join("\n",
                "PREFIX ex: <http://example.org/>",
                "SELECT ?act {",
                "ex:car_rental ex:hasLocation ?act . }");

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < n; ++i) {
            Query query = QueryFactory.create(queryString);
            QueryExecution qExec = QueryExecutionFactory.create(query, model);

            if (DEBUG) {
                for (ResultSet itr = qExec.execSelect(); itr.hasNext(); ) {
                    QuerySolution next = itr.next();
                    Log.d("RESULT RDF", next.get("act").toString());
                }
            }
            qExec.close();
        }
        graph.close();

        long stopTime = System.currentTimeMillis();

        return stopTime - startTime;
    }

    /**
     * Benchmark querying on Shared Preferences
     *
     * @param context Context of the Android app.
     * @param n Number of queries to run.
     * @return Time in milliseconds to execute queries.
     */
    private static long benchmarkRegularQuery(Context context, int n) {
        String location = "car_rental";

        SharedPreferences settings = context.getSharedPreferences("triples", 0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < n; ++i) {
            Set<String> stringSet = settings.getStringSet(location, new HashSet<String>());

            if (DEBUG) {
                Log.d("RESULT SP", stringSet.toString());
            }
        }

        long stopTime = System.currentTimeMillis();

        return stopTime - startTime;
    }

    /**
     * Benchmark querying on SQLite
     *
     * @param context Context of the Android app.
     * @param n Number of queries to run.
     * @return Time in milliseconds to execute queries.
     */
    public static long benchmarkDBQuery(Context context, int n) throws IOException {
        BufferedReader buf = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(R.raw.triples)));

        SQLiteDatabase table = db.getReadableDatabase();

        long startTime = System.currentTimeMillis();

        String[] selection = {"activity"};
        for (int i = 0; i < n; ++i) {
            Cursor result =
                    table.rawQuery("SELECT activity FROM LocationMapping WHERE location='car_rental'", null);

            if (DEBUG) {
                result.moveToFirst();
                while (!result.isAfterLast()) {
                    if (result.getColumnIndex("activity") == 0)
                        Log.d("RESULT DB", String.valueOf(result.getString(0)));
                    result.moveToNext();
                }
            }
            result.close();
        }

        long stopTime = System.currentTimeMillis();

        return stopTime - startTime;
    }

    /**
     * Benchmark inserting on RDF On the Go
     *
     * @param context Context of the Android app.
     * @param numLines Number of queries to run.
     * @return Time in milliseconds to execute queries.
     */
    public static long benchmarkRDFStore(Context context, int numLines) throws IOException {
        String predicate = "http://example.org/hasLocation";
        BufferedReader buf = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(R.raw.triples)));

        BDBGraph graph = new BDBGraph("benchmark2");

        long startTime = System.currentTimeMillis();

        for (String line = buf.readLine(); numLines > 0 && line != null; --numLines, line = buf.readLine()) {
            String[] values = line.split("\\s+");

            Node s = Node.createURI("http://example.org/" + values[0]);
            Node p = Node.createURI(predicate);
            Node o = Node.createLiteral(values[1]);

            Triple t = new Triple(s, p, o);
            graph.add(t);
        }

        graph.sync();
        graph.close();

        long stopTime = System.currentTimeMillis();

        return stopTime - startTime;
    }

    /**
     * Benchmark storing on Shared Preferences
     *
     * @param context Context of the Android app.
     * @param numLines Number of queries to run.
     * @return Time in milliseconds to execute queries.
     */
    public static long benchmarkRegularStore(Context context, int numLines) throws IOException {
        BufferedReader buf = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(R.raw.triples)));

        SharedPreferences settings = context.getSharedPreferences("triples", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();

        long startTime = System.currentTimeMillis();

        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        for (String line = buf.readLine(); numLines > 0 && line != null; --numLines, line = buf.readLine()) {
            String[] values = line.split("\\s+");

            if (map.containsKey(values[1])) {
                Set<String> s = map.get(values[1]);
                s.add(values[0]);

                map.put(values[1], s);
            } else {
                Set<String> s = new HashSet<String>();
                s.add(values[0]);

                map.put(values[1], s);
            }
        }

        for (Map.Entry<String, Set<String>> e : map.entrySet()) {
            editor.putStringSet(e.getKey(), e.getValue());
        }
        editor.commit();

        long stopTime = System.currentTimeMillis();

        return stopTime - startTime;
    }

    /**
     * Benchmark storing on SQLite
     *
     * @param context Context of the Android app.
     * @param numLines Number of queries to run.
     * @return Time in milliseconds to execute queries.
     */
    public static long benchmarkDBStore(Context context, int numLines) throws IOException {
        BufferedReader buf = new BufferedReader(new InputStreamReader(
                    context.getResources().openRawResource(R.raw.triples)));

        SQLiteDatabase table = db.getWritableDatabase();
        table.delete("LocationMapping", null, null);

        long startTime = System.currentTimeMillis();
                                                                           SQLiteOpenHelper db = new MappingOpenHelper(context);
        for (String line = buf.readLine(); numLines > 0 && line != null; --numLines, line = buf.readLine()) {
            String[] vals = line.split("\\s+");
            ContentValues values = new ContentValues();
            values.put("location", vals[1]);
            values.put("activity", vals[0]);

            table.insert("LocationMapping", null, values);
        }

        long stopTime = System.currentTimeMillis();

        return stopTime - startTime;
    }
}
