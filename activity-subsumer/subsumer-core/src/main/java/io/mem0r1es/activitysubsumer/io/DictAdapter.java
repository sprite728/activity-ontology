package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.utils.LineByLineSplit;
import io.mem0r1es.activitysubsumer.utils.Pair;
import io.mem0r1es.activitysubsumer.utils.SubConf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.NoSuchElementException;

/**
 * Reads the words, and saves them to the dictionary. The entire list of words can be used
 * for searching, and because of that this is separated from processing children, parents, and
 * synset code - word files.
 *
 * @author Ivan Gavrilović
 */
public class DictAdapter {
    private LineByLineSplit wordReader;

    /**
     * @param wordStream   stream to word - synset code pairs
      */
    public DictAdapter(InputStream wordStream){
        this.wordReader = new LineByLineSplit(wordStream);
    }

    public static DictAdapter getDefaultVerbs() throws IOException{
        return new DictAdapter(new FileInputStream(SubConf.CONFIG.getVerbsWords()));
    }

    public static DictAdapter getDefaultNouns() throws IOException{
        return new DictAdapter(new FileInputStream(SubConf.CONFIG.getNounsWords()));
    }

    public Pair<String, Integer> word() throws IOException {
        if (wordReader.hasNext()) {
            String[] parts = wordReader.next();
            if (parts.length == 2) {
                String newWord = URLDecoder.decode(parts[0], "UTF-8");
                Integer synsetCode = Integer.parseInt(parts[1]);

                return Pair.get(newWord, synsetCode);
            } else {
                throw new IOException("Unexpected file format. Each line should have word - code syntax");
            }
        } else throw new NoSuchElementException();
    }

    public boolean hasWord() {
        return wordReader.hasNext();
    }

    public void closeWord() {
        wordReader.close();
    }
}
