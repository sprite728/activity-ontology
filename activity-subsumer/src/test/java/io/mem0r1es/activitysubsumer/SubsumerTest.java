package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.useractivitytree.core.Subsumer;
import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;

/**
 * Created by George on 9/26/13.
 */
public class SubsumerTest extends TestCase {
  private static final String TAG = Subsumer.class.getCanonicalName();

  public void testJavaSerialization() throws Exception {
    long startTime = System.currentTimeMillis();
    Subsumer subsumer = new Subsumer(ClassLoader.getSystemResourceAsStream("nouns.graph"),
        ClassLoader.getSystemResourceAsStream("verbs.graph"));

    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime - startTime;
    System.out.println(new Date() + " " + new Date() + " " + "Create Subsumer Time:" + elapsedTime);
    startTime = System.currentTimeMillis();
    BufferedReader br;
    try {
      System.out.println(new Date() + " " + "start reading activities");
      br = new BufferedReader(new InputStreamReader(
          ClassLoader.getSystemResourceAsStream("activities.graph")));

      String line;
      while ((line = br.readLine()) != null) {
        System.out.println(new Date() + " " + "start add activity");
        String verb = null, noun = null, location = null, timeOfDay = null, avgDur = null;
        StringTokenizer tok = new StringTokenizer(line, ",");
        verb = tok.nextToken();
        noun = tok.nextToken();
        location = tok.nextToken();
        timeOfDay = tok.nextToken();
        avgDur = tok.nextToken();
        UserActivity act = new UserActivity(verb, noun, location, timeOfDay, avgDur);
        subsumer.addActivity(act);
        System.out.println(new Date() + " " + "end add activity");
        System.out.println(new Date() + " " + subsumer.toString());
      }
      stopTime = System.currentTimeMillis();
      elapsedTime = stopTime - startTime;
      System.out.println(new Date() + " " + "Add activities Time:" + elapsedTime);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    List<UserActivity> l = subsumer.getActivitiesForLocation("hospital");

    /*
     * Log.d(TAG, "deserialize tree start"); Subsumer subsumer1 =
     * readFile(FILENAME); Log.d(TAG, "deserialize tree end");
     */
    // getContext().deleteFile(FILENAME);
  }
}
