import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Philosopher {
	
	public static final File logFile = new File("src/Log.txt");
	public static FileWriter log;
	public static long startTime;

	
	enum State{
			THINKING,
			EATING,
			HUNGRY
	}
	
	private int id;
	private State status;
	private int numberOfEating;
	private int numberOfEatingTime;
	private int numberOfThinking;
	private int numberOfThinkingTime;
	private int numberOfHungry;
	private int numberOfHungryTime;
	private long seed = 1000000;	// default one million
	

	public Philosopher(int id){
		this.id = id;
		status = State.THINKING;
		numberOfEating = 0;
		numberOfEatingTime = 0;
		numberOfThinking = 0;
		numberOfThinkingTime = 0;
		numberOfHungry = 0;
		numberOfHungryTime = 0;
		
	}
	
	public State getStatus(){
		return status;
	}
	public void setStatus(State status){
		this.status = status;
	}
	public int getId(){
		return id;
	}
	public double getAverageThinkingTime(){
		if (numberOfThinking == 0) return 0;
		return (numberOfThinkingTime/numberOfThinking);
	}
	public double getAverageEatingTime(){
		if (numberOfEating == 0) return 0;
		return (numberOfEatingTime/numberOfEating);
	}
	public double getAverageHungryTime(){
		if (numberOfHungry == 0) return 0;
		return (numberOfHungryTime/numberOfHungry);
	}
	
	public int getNumberOfEatingTurns(){
		return numberOfEating;
	}
	public void increaseHungryTime(int i){
		numberOfHungryTime += i;
	}
	
	public void think(){
		int time = waitRandom(State.THINKING);
		numberOfThinking++;
		try {
			synchronized (logFile){
				log.write((System.currentTimeMillis()-startTime) + "ms : Philosopher_"+id+" is " + State.THINKING + " for " + time + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void eat(){
		int time = waitRandom(State.EATING);
		numberOfEating++;
		try {
			synchronized (logFile){
				log.write((System.currentTimeMillis()-startTime) + "ms : Philosopher_"+id+" is " + State.EATING +  " for " + time + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void hungry(){
		numberOfHungry++;
		try {
			synchronized (logFile){
				log.write((System.currentTimeMillis()-startTime) + "ms : Philosopher_"+id+" is " + State.HUNGRY + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int waitRandom(State status){
		Random rand = new Random(seed);
		int time = rand.nextInt(10)+1;
		try {
			Thread.sleep(time);
			} catch (InterruptedException e) {
			e.printStackTrace();
			}
		// Increase average time
		if (status == State.THINKING){
			numberOfThinkingTime += time;
		}
		else if (status == State.EATING){
			numberOfEatingTime += time;
		}
		return time;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}
	
	// debug
	public int getNumberOfEating() {
		return numberOfEating;
	}
	public int getNumberOfEatingTime() {
		return numberOfEatingTime;
	}
	public int getNumberOfThinking() {
		return numberOfThinking;
	}
	public int getNumberOfThinkingTime() {
		return numberOfThinkingTime;
	}
	public int getNumberOfHungry() {
		return numberOfHungry;
	}
	public int getNumberOfHungryTime() {
		return numberOfHungryTime;
	}


	
		
	

}
