package io.mem0r1es.activitysubsumer.useractivitytree.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.graph.DefaultEdge;

import io.mem0r1es.activitysubsumer.useractivitytree.core.UserActivityGraph;

public class CSVExporter {
	public void exportCSV(String fileName, UserActivityGraph graph) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Source;Target\n");
		for (DefaultEdge edge : graph.getEdges()) {
			stringBuilder.append(graph.getEdgeSource(edge) + ";" + graph.getEdgeTarget(edge) + "\n");
		}

		FileWriter writer = new FileWriter(new File(fileName));
		writer.write(stringBuilder.toString());
		writer.close();
	}
}