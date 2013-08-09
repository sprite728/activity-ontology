package ch.epfl.lsir.memories.android_places.activities;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import ch.epfl.lsir.memories.android_places.test.benchmark.BenchmarkStorage;

import java.io.IOException;

/**
 * @author Sebastian Claici
 */
public class BenchmarkActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BenchmarkClass backgroundWorker = new BenchmarkClass(this);
        backgroundWorker.execute();
    }

    class BenchmarkClass extends AsyncTask<Void, Void, Void> {
        private final Context context;

        BenchmarkClass(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                BenchmarkStorage.runTest(context);
            } catch (IOException e) {}

            return null;
        }
    }
}