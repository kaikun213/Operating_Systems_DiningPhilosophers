import java.util.ArrayList;

public class Program {

	public static void main(String[] args) {
		DiningPhilosopher dp = new DiningPhilosopher();
		dp.setSimulationTime(1000000);
		dp.initialize();
		dp.start();
		ArrayList<Philosopher> philosophers = dp.getPhilosophers();
		for (Philosopher p : philosophers) {
			System.out.println("Philosopher " + p.getId() + "'s average thinking time: \t" + p.getAverageThinkingTime());
			System.out.println("Philosopher " + p.getId() + "'s thinking times: \t" + p.getNumberOfThinking());
			System.out.println("Philosopher " + p.getId() + "'s thinking time: \t\t" + p.getNumberOfThinkingTime());
			
			System.out.println("Philosopher " + p.getId() + "'s average eating time: \t" + p.getAverageEatingTime());
			System.out.println("Philosopher " + p.getId() + "'s eating times: \t\t" + p.getNumberOfEating());
			System.out.println("Philosopher " + p.getId() + "'s eating time: \t\t" + p.getNumberOfEatingTime());
			
			System.out.println("Philosopher " + p.getId() + "'s average hungry time: \t" + p.getAverageHungryTime());
			System.out.println("Philosopher " + p.getId() + "'s hungry times: \t\t" + p.getNumberOfHungry());
			System.out.println("Philosopher " + p.getId() + "'s hungry time: \t\t" + p.getNumberOfHungryTime()+ "\n");
		}
	}
	
}
