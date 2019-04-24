package problems.report;

import java.io.IOException;

import problems.log.Log;
import problems.qbfpt.solvers.GA_QBFPT;
import solutions.Solution;

public class Report {
	
	static String[] INSTANCES = {
			"instances/qbf020",
			"instances/qbf040",
			"instances/qbf060",
			"instances/qbf080",
			"instances/qbf100",
			"instances/qbf200",
			"instances/qbf400",
		}; 
	
	static Integer[] INTERATIONS = {
			1000,
			100000,
			10000000,
			10000000,
			10000000,
			10000000,
			10000000,
		}; 
	
	static Integer[] POPULATION_SIZE = {
			100,
			1000
	};
	
	static Double[] MUTATION_RATE = {
			1.0,
			3.0
	};
	
	private static synchronized String getLogFileName(String prefix, int i, int j, int t) {
		String instance = INSTANCES[i].replace("instances/", "");
		String pop = POPULATION_SIZE[j].toString();
		String rate = MUTATION_RATE[t].toString();
		return "report_"+prefix+"-"+instance+"-"+pop+"-"+rate+"-"+i+"-"+j+"-"+j+".log";
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		Boolean runStandard = Boolean.TRUE;
		Boolean runUniformCrossover = Boolean.FALSE;
		Boolean runSteadyState = Boolean.FALSE;
		
		if (runStandard) {
			for (int i = 0; i < INSTANCES.length; i++) 
			for (int j = 0; j < POPULATION_SIZE.length; j++) 
			for (int t = 0; t < MUTATION_RATE.length; t++) {
				final int z = i;
				final int h = j;
				final int v = t;
				gaStandardJob(z, h, v);
			}
		}
		
		if (runSteadyState) {
			/*
			 *  TODO: SteadyState
			 */
		}
		
		if (runUniformCrossover) {
			/*
			 *  TODO: UniformCrossover
			 */
		}
		
	}
	
	private static void gaStandardJob(final int z, final int h, final int v) {
		final String fileLogName = getLogFileName("standard",z,h,v);
		try {
			Thread.currentThread().setName(fileLogName);
			System.out.println("Thread: "+Thread.currentThread().getName()+ " start!");
			Log.getLogger(fileLogName).info("{\n\tInstancia		: "+INSTANCES[z]);
			Log.getLogger(fileLogName).info("\tInterações		: "+INTERATIONS[z]);
			Log.getLogger(fileLogName).info("\tTamanho pop.		: "+POPULATION_SIZE[h]);
			Log.getLogger(fileLogName).info("\tMutação taxa		: "+MUTATION_RATE[v]);
			Log.getLogger(fileLogName).info("\tAlgoritmo		: Padrão");
			GA_QBFPT solver = new GA_QBFPT(INTERATIONS[z], POPULATION_SIZE[h], MUTATION_RATE[v]/POPULATION_SIZE[h], INSTANCES[z]);
			Solution<Integer> solve = solver.solve();
			Log.getLogger(fileLogName).info(solver.getReport());
			Log.getLogger(fileLogName).info("\t\tmaxVal = " + solve);
			Log.getLogger(fileLogName).info("\n}");
		} catch (IOException e) {
			Log.getLogger(fileLogName).info("Erro: "+e.getStackTrace().toString());
			e.printStackTrace();
		}
		System.out.println("Thread: "+Thread.currentThread().getName()+ "end!");
	}

}
