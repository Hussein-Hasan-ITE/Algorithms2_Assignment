import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class Network {
	private int numberOfStations;

	private class Station{
		private String name;
		private String ID;
		private HashMap<Station , RailWay> railWays = new HashMap<>();

		public Station (String name){
			this.name = name;
			ID = "s" + numberOfStations;
		}

		public void addRailWay(Station to , int distance){
			railWays.putIfAbsent(to , new RailWay(this , to , distance));
		}

		@Override
		public String toString() {
			return name + railWays.values() ;
		}

		public Map<Station , RailWay> getRailWays(){
			return railWays;
		}
	}

	private class RailWay{
		private Station from;
		private Station to;
		private int distance;

		public RailWay(Station from , Station to , int distance){
			this.from = from;
			this.to = to;
			this.distance = distance;
		}

		@Override
		public String toString() {
			return from.name + " --(" + distance + ")--> " + to.name;
		}
	}

	HashMap<String , Station> stations = new HashMap<>();

	public Network(){}

	public Network(HashMap<String , ArrayList<Pair<String , Integer>>> connections){
		for(var station : connections.keySet()){
			this.stations.putIfAbsent(station , new Station(station));
			numberOfStations++;
			for(var destination : connections.get(station)){
				if (stations.containsKey(destination.first)){
					stations.get(station).addRailWay(stations.get(destination.first) , destination.second);
				}
				else {
					stations.put(destination.first , new Station(destination.first));
					numberOfStations++;
					stations.get(station).addRailWay(stations.get(destination.first) , destination.second);
				}
			}
		}
	}

	public HashMap<String , ArrayList<Pair<String , Integer>>> toExport(){
		HashMap<String , ArrayList<Pair<String , Integer>>> toReturn = new HashMap<>();
		for(var station : stations.keySet()){
			toReturn.put(station , new ArrayList<>());
			for(var connection : stations.get(station).getRailWays().values()){
				toReturn.get(station).add(new Pair<>(connection.to.name , connection.distance));
			}
		}
		return toReturn;
	}

	public void exportNetworkToFile(){
		try{
			W_RFiles.exportToFile(toExport());
		}
		catch(IOException e){
			System.out.println("file exception");
		}
	}

	public void addStation(String name){
		stations.putIfAbsent(name , new Station(name));
		numberOfStations++;
	}

	public void addRailWay(String from , String to , int distance){
		stations.get(from).addRailWay(stations.get(to) , distance);
	}

	public boolean removeStation(String name){
		if(stations.get(name) == null)
			return false;

		for(var station : stations.values()){
			station.getRailWays().remove(stations.get(name));
		}
		stations.remove(name);
		return true;
	}

	public boolean removeRailWay(String from , String to){
		if(stations.get(from) == null || stations.get(to) == null){
			return false;
		}
		stations.get(from).railWays.remove(stations.get(to));
		return true;
	}

	public void print(){
		System.out.println(stations.values());
	}

	private class PrioritizedStation {
		Station station;
		int priority;

		public PrioritizedStation(String name , int priority){
			this.station = stations.get(name);
			this.priority = priority;
		}

		public PrioritizedStation(Station station , int priority){
			this.station = station;
			this.priority = priority;
		}
	}

	public int getShortestDistance(String from , String to){
		if (stations.get(from) == null || stations.get(to) == null) {
			return -1;
		}
		PriorityQueue<PrioritizedStation> stationsToVisit = new PriorityQueue<>(
				Comparator.comparingInt(ps -> ps.priority)
		);
		HashSet<Station> visited = new HashSet<>();
		HashMap<Station , Integer> distances = new HashMap<>();
		for(var station : stations.values()){
			distances.put(station , Integer.MAX_VALUE);
		}
		distances.replace(stations.get(from) , 0);
		stationsToVisit.add(new PrioritizedStation(from , 0));

		while (!stationsToVisit.isEmpty()){
			Station current = stationsToVisit.remove().station;
			visited.add(current);

			for(var railWay : current.getRailWays().values()){
				if(visited.contains(railWay.to))
					continue;
				int currentDistance = distances.get(current) + railWay.distance;
				if (currentDistance < distances.get(railWay.to)) {
					distances.replace(railWay.to , currentDistance);
					stationsToVisit.add(new PrioritizedStation(railWay.to , currentDistance));
				}
			}
		}
		return distances.get(stations.get(to)) == Integer.MAX_VALUE ? -1 : distances.get(stations.get(to));
	}

	public ArrayList<String> getShortestPath(String from , String to){
		if (stations.get(from) == null || stations.get(to) == null) {
			return new ArrayList<>();
		}
		PriorityQueue<PrioritizedStation> stationsToVisit = new PriorityQueue<>(
				Comparator.comparingInt(ps -> ps.priority)
		);
		HashSet<Station> visited = new HashSet<>();
		HashMap<Station , Integer> distances = new HashMap<>();
		HashMap<Station , Station> previous = new HashMap<>();
		for(var station : stations.values()){
			distances.put(station , Integer.MAX_VALUE);
		}
		distances.replace(stations.get(from) , 0);
		stationsToVisit.add(new PrioritizedStation(from , 0));

		while (!stationsToVisit.isEmpty()){
			Station current = stationsToVisit.remove().station;
			visited.add(current);

			for(var railWay : current.getRailWays().values()){
				if(visited.contains(railWay.to))
					continue;
				int currentDistance = distances.get(current) + railWay.distance;
				if (currentDistance < distances.get(railWay.to)) {
					distances.replace(railWay.to , currentDistance);
					previous.put(railWay.to , current);
					stationsToVisit.add(new PrioritizedStation(railWay.to , currentDistance));
				}
			}
		}
		var toReturn = new ArrayList<String>();
		var current = stations.get(to);
		while(current != null){
			toReturn.addFirst(current.name);
			current = previous.get(current);
		}
		return Objects.equals(toReturn.getFirst(), from) ? toReturn : new ArrayList<String>();
	}

}
