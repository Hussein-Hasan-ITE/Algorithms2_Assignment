import java.io.IOException;

public class Main {
	public static void main(String[] args){
		try {
			Network n = new Network(W_RFiles.importFromFile());
			n.print();
			n.exportNetworkToFile();
		}
		catch (IOException e){
			System.out.println("file exception");
		}
	}
}
