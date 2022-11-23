package Assignment2;

import general.Replication;
import general.Simulation;
import general.automagic.AutoReplication;

public class MainConcertNH {

	public static void main(String[] args) {
		// parameters
		int standingCapacity = 6300;
		int seatingCapacity = 10700;
		double mu = 3600.0/10.0; // hourly service rate

		double timeHorizon = 1000;
		long n = 10000;
		long seed = 0;

		for (int i = 1; i <= 50; i++) {
			int nScanners = i;
			public ConcertStateNH(nScanners, standingCapacity, seatingCapacity, mu,
			lambdaStanding, lambdaSeated, time, timeHorizon, seed);
			Replication<ConcertStateNH> replication = new AutoReplication<ConcertStateNH>(state);

			Simulation<ConcertStateNH> simulation = new Simulation<>(replication);
			simulation.run(n);
			System.out.println("nScanners: " + nScanners);
			simulation.printEstimates();
			System.out.println("--------------------------------------------------------------------------------------------------------");
		}
	}

	}
