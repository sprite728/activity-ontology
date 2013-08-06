package ch.epfl.lsir.memories.android_places.test.rdf;

import ch.epfl.lsir.memories.android_places.utils.Utils;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import deri.org.store.BDBGraph;

/**
 * @author Sebastian Claici
 */
public class QueryTest {

    public static void main(String[] args) {
        BDBGraph graph = new BDBGraph("store");

        ModelCom model = new ModelCom(graph);
        String queryString = Utils.join("\n",
                "PREIFX foaf: <http://xmlns.com/foaf/0.1/>",
                "SELECT DISTINCT ?name {",
                "?uri foaf:name ?name.",
                "}");

        Query query = QueryFactory.create(queryString);
        QueryExecution qExec = QueryExecutionFactory.create(query, model);

        for (ResultSet itr = qExec.execSelect(); itr.hasNext(); ) {
            QuerySolution sol = itr.next();
            System.out.println(sol.get("name").toString());
        }

        graph.close();
    }
}
