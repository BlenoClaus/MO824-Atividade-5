package problems.qbfpt.solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import problems.log.Log;
import problems.qbf.solvers.GA_QBF;
import problems.qbfpt.triple.ForbiddenTriplesBuilder;
import solutions.Solution;

public class GA_QBFPT extends GA_QBF {

	private ForbiddenTriplesBuilder ftBuilder;
	private StringBuilder report = new StringBuilder();

	public GA_QBFPT(Integer generations, Integer popSize, Double mutationRate, String filename) throws IOException {
		super(generations, popSize, mutationRate, filename);
		this.ftBuilder = new ForbiddenTriplesBuilder(ObjFunction.getDomainSize());
	}
	
	@Override
	protected Solution<Integer> decode(Chromosome chromosome) {
		Solution<Integer> solution = createEmptySol();
		for (int locus = 0; locus < chromosome.size(); locus++) {
			if (chromosome.get(locus) == 1) {
				solution.add(new Integer(locus));
				
				List<Integer> forbiddenValues = new ArrayList<>();
				Integer lastElem = solution.get(solution.size()-1);
				for (int i = 0; i < solution.size()-1; i++) {
					forbiddenValues.addAll(ftBuilder.getForbiddenValues(solution.get(i)+1, lastElem+1));
				}
				for (Integer fv : forbiddenValues) {
					int index = solution.indexOf(fv-1);
					if (index >= 0) solution.remove(index);
				}
			}
		}
		ObjFunction.evaluate(solution);
		return solution;
	}
	
	@Override
	protected Double fitness(Chromosome chromosome) {
		if (forbiddenChromosome(chromosome))
			return Double.NEGATIVE_INFINITY;
		return decode(chromosome).cost;
	}
	
	@Override
	protected Chromosome getBestChromosome(Population population) {
		double bestFitness = Double.NEGATIVE_INFINITY;
		Chromosome bestChromosome = null;
		for (Chromosome c : population) {
			if (!forbiddenChromosome(c)) {
				double fitness = fitness(c);
				if (fitness > bestFitness) {
					bestFitness = fitness;
					bestChromosome = c;
				}
			}
		}
		return bestChromosome;
	}
	
	@Override
	protected Chromosome getWorseChromosome(Population population) {
		double worseFitness = Double.POSITIVE_INFINITY;
		Chromosome worseChromosome = null;
		for (Chromosome c : population) {
			if (forbiddenChromosome(c)) return c;
			double fitness = fitness(c);
			if (fitness < worseFitness) {
				worseFitness = fitness;
				worseChromosome = c;
			}
		}
		return worseChromosome;
	}
	
	private boolean forbiddenChromosome(Chromosome chromosome) {
		List<Integer> forbiddenValues = new ArrayList<>();
		for (int i = 0; i < chromosomeSize; i++) {
			for (int j = 0; j < chromosomeSize; j++) {
				if (i == j) continue;
				int z = chromosome.get(i);
				int m = chromosome.get(j);
				if (z == 1 && m == 1) {
					forbiddenValues.addAll(ftBuilder.getForbiddenValues(i+1, j+1));
				}
			}
		}
		
		for (int i = 0; i < chromosomeSize; i++) {
			Integer lacus = chromosome.get(i);
			if (lacus == 1) {
				if (forbiddenValues.contains(lacus+1)) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}
	
	@Override
	public Solution<Integer> solve() {
		long startTime = System.currentTimeMillis();
		double thirtyMinutes = 30 * 60;
		double totalTempo = 0.0;
		/* starts the initial population */
		Population population = initializePopulation();
		bestChromosome = getBestChromosome(population);
		bestSol = decode(bestChromosome);
		report.append("\t\t(Gen. " + 0 + ") BestSol = " + bestSol+"\n");
		

		/*
		 * enters the main loop and repeats until a given number of generations
		 */
		for (int g = 1; g <= generations && totalTempo < 1.2*thirtyMinutes; g++) {
			Population parents = selectParents(population);
			Population offsprings = crossover(parents);
			Population mutants = mutate(offsprings);
			Population newpopulation = selectPopulation(mutants);
			population = newpopulation;
			bestChromosome = getBestChromosome(population);

			if (fitness(bestChromosome) > bestSol.cost) {
				bestSol = decode(bestChromosome);
				if (verbose)
					report.append("\t\t(Gen. " + g + ") BestSol = " + bestSol+"\n");
			}
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			totalTempo = (double)totalTime/(double)1000;
		}
		return bestSol;
	}
	
	public String getReport() {
		return report.toString();
	}
	
	private static String getLogFileName(String prefix, int i, int j, int t) {
		String instance = "qbf020";
		String pop = "100";
		String rate = "100";
		return "report_"+prefix+"-"+instance+"-"+pop+"-"+rate+"-"+i+"-"+j+"-"+j+".log";
	}
	
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		String fileLogName = getLogFileName("teste",1,1,1);
		Log.getLogger(fileLogName).info("{\n\tInstancia		: instances/qbf020");
		Log.getLogger(fileLogName).info("\tInterações		: 100");
		Log.getLogger(fileLogName).info("\tTamanho pop.		: 100");
		Log.getLogger(fileLogName).info("\tMutação taxa		: "+1.0/100.0);
		Log.getLogger(fileLogName).info("\tAlgoritmo		: Padrão");
		GA_QBF ga = new GA_QBFPT(100, 100, 1.0 / 100.0, "instances/qbf020");
		Solution<Integer> bestSol = ga.solve();
		Log.getLogger(fileLogName).info("maxVal = " + bestSol);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");
	}

}
