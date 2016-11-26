import java.util.ArrayList;

public class Program {

	public static void main(String[] args) {
		DiningPhilosopher dp = new DiningPhilosopher();
		dp.setSimulationTime(10000);
		dp.initialize();
		dp.start();
		ArrayList<Philosopher> philosophers = dp.getPhilosophers();
		for (Philosopher p : philosophers) {
		System.out.println(p.getId() + "- " + p.getAverageThinkingTime());

		}
	}

}
