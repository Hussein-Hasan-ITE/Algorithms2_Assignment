import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class W_RFiles {
	public static HashMap<String , ArrayList<Pair<String , Integer>>> importFromFile() throws IOException {
		HashMap<String , ArrayList<Pair<String , Integer>>> network = new HashMap<>();

		BufferedReader reader = new BufferedReader(new FileReader("toReadNetwork.txt"));
		String line;

		while ((line = reader.readLine()) != null) {

			String[] parts = line.split("->");
			String source = parts[0].trim();

			network.put(source , new ArrayList<>());

			String[] edges = parts[1].trim().split(",");

			for (String edge : edges) {
				edge = edge.trim();
				String[] edgeParts = edge.split("\\(");
				String destName = edgeParts[0].trim();
				String weight = edgeParts[1].replace(")", "").trim();
				int weightInt = Integer.parseInt(weight);
				network.get(source).add(new Pair<>(destName , weightInt));

			}
		}
		reader.close();
		return network;
	}

	public static void exportToFile(HashMap<String , ArrayList<Pair<String , Integer>>> network) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("toWriteNetwork.txt"));

		for(var station : network.keySet()){
			if(network.get(station).isEmpty())
				continue;
			writer.write(station + " -> ");
			for(int i = 0 ; i < network.get(station).size() ; i++){
				if(i < network.get(station).size() - 1){
					writer.write(network.get(station).get(i).first + "(" + network.get(station).get(i).second + ") , ");
				}
				else {
					writer.write(network.get(station).get(i).first + "(" + network.get(station).get(i).second + ")");
				}
			}
			writer.newLine();
		}

		writer.close();
	}
}
