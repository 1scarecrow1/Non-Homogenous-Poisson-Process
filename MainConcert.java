package Assignment2;

import general.Replication;
import general.Simulation;
import general.automagic.AutoReplication;

public class MainConcert {

	public static void main(String[] args) {
		// parameters

		int standingCapacity = 6300;
		int seatingCapacity = 10700;
		double mu = 3600.0/10.0; // hourly service rate
		int lambdaStanding = 2500;
		int lambdaSeated = 4000;
		double timeHorizon = 1000;
		long n = 10000;
		long seed = 0;
		
		for (int i = 1; i <= 50; i++) {
			int nScanners = i;
			ConcertState state = new ConcertState(nScanners, standingCapacity, seatingCapacity, mu, lambdaStanding,
					lambdaSeated, timeHorizon, seed);
			Replication<ConcertState> replication = new AutoReplication<ConcertState>(state);

			Simulation<ConcertState> simulation = new Simulation<>(replication);
			simulation.run(n);
			System.out.println("nScanners: " + nScanners);
			simulation.printEstimates();
			System.out.println("--------------------------------------------------------------------------------------------------------");
		}
	}
}
