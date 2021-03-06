package nl.bknopper.tspeademo.ea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import nl.bknopper.tspeademo.util.TSPUtils;

public class Algorithm {

    private static final Random RANDOM = new Random();

    /**
     * Population of the algorithm
     */
    private List<CandidateSolution> population = new ArrayList<CandidateSolution>();

    /**
     * Thread in which the algorithm runs
     */
    private Thread algorithmThread;

    /**
     * Probability with which a new child mutates
     */
    private int mutationProbability;

    /**
     * Size of the population (as given by the GUI)
     */
    private int populationSize;

    /**
     * number of generations to run max.
     */
    private int nrOfGenerations;

    /**
     * When this fitness threshold is reached the algorithm can be stopped
     */
    private int fitnessThreshold;

    /**
     * Number of parents to be selected for reproduction (each generation)
     */
    private int parentSelectionSize;

    /**
     * Pool of CandidateSolution from which a number of parents will be selected
     * for reproduction (each generation)
     */
    private int parentPoolSize;

    private volatile boolean running = true;

    /**
     * Constructor for the Evolutionary Algorithm
     *
     * @param mutationProbability
     * @param populationSize
     * @param nrOfGenerations
     * @param fitnessThreshold
     * @param parentSelectionSize
     * @param parentPoolSize
     */
    public Algorithm(int mutationProbability,
                     int populationSize, int nrOfGenerations, int fitnessThreshold,
                     int parentSelectionSize, int parentPoolSize) {
        this.mutationProbability = mutationProbability;
        this.populationSize = populationSize;
        this.nrOfGenerations = nrOfGenerations;
        this.fitnessThreshold = fitnessThreshold;
        this.parentSelectionSize = parentSelectionSize;
        this.parentPoolSize = parentPoolSize;
    }

    /**
     * Starts the algorithm using the settings given through the constructor.
     */
    public void startAlgorithm() {

	    /* let the algorithm run in a Thread */
        algorithmThread = new Thread(new Runnable() {
            public void run() {
                population = initialisation();

                /*
                 * implemented a Comparable for CandidateSolution using its
                 * fitness. So best fitness (lowest) first.
                 */
                Collections.sort(population);
                CandidateSolution bestCandidateSolution = population.get(0);

                int generations = 0;

		        /* start the iterative part of the algorithm */
                while (generations != nrOfGenerations
                        && population.get(0).getFitness() > fitnessThreshold && running) {

		            /* Select the parents for reproduction */
                    List<CandidateSolution> parents = parentSelection();

                    /* Let the selected parents reproduce (recombine) */
                    List<CandidateSolution> offspring = createOffspring(parents);
                    population.addAll(offspring);

                    /*
                     * Since evaluation of candidate solutions is done within
                     * the CandidateSolution itself, there is no need to
                     * evaluate seperately here (although that is a part of the
                     * Evolutionary Algorithm)
                     */

                    /*
                     * Survivor selection: which individuals
                     * (CandidateSolutions) progress to the next generation
                     */
                    selectSurvivors();

                    generations++;

                    for(CandidateSolution solution : population) {
                        solution.setGeneration(generations);
                    }
                    if (generations % 100 == 0) {
                        System.out.println("Thread: " + Thread.currentThread().getName()+ ", Generations: " + generations);
                    }

                }
                running = false;
            }
        });

	    /* start the above defined algorithm */
        algorithmThread.start();
    }

    private List<CandidateSolution> createOffspring(List<CandidateSolution> parents) {
        List<CandidateSolution> offspring = new ArrayList<>(parents.size());
        for (int i = 0; i < parents.size(); i += 2) {
            CandidateSolution parent1 = parents.get(i);
            CandidateSolution parent2 = parents.get(i + 1);

            List<CandidateSolution> children = parent1
                    .recombine(parent2);

            /*
             * let the children mutate with probability
             * mutationProbability and add them to the population
             */
            for (CandidateSolution child : children) {

                /* probability to mutate */
                if (RANDOM.nextInt(101) <= mutationProbability) {
                    child.mutate();
                }

                offspring.add(child);
            }
        }
        return offspring;
    }

    /**
     * Selects the survivors by removing the worst candidate solutions from the
     * list, so we have the original population size again
     */
    private void selectSurvivors() {

	    /* Sort the population so that the best candidates are up front */
        Collections.sort(population);

        /*
         * cut back the population to the original size by dropping the worst
         * candidates
         */
        population = new ArrayList<CandidateSolution>(population.subList(0,
                populationSize));

    }

    /**
     * Select the x best candidate solutions from a randomly selected pool from
     * the population
     *
     * @return parents a list of the chosen parents
     */
    private List<CandidateSolution> parentSelection() {

        List<CandidateSolution> tempPopulation = new ArrayList<CandidateSolution>(
                population);
        List<CandidateSolution> randomCandidates = new ArrayList<CandidateSolution>(parentPoolSize);

        /* create parent pool */
        for (int i = 0; i < parentPoolSize; i++) {

	        /* select a random candidate solution from the temp population */
            int randomlySelectedIndex = RANDOM.nextInt(tempPopulation.size());
            CandidateSolution randomSelection = tempPopulation
                    .get(randomlySelectedIndex);

            randomCandidates.add(randomSelection);

            /*
             * delete the candidate from the temp population, so we can't pick
             * it again
             */
            tempPopulation.remove(randomlySelectedIndex);
        }

	    /* Sort the population so that the best candidates are up front */
        Collections.sort(randomCandidates);

        /*
         * return a list with size parentSelectionSize with the best
         * CandidateSolutions
         */
        return randomCandidates.subList(0, parentSelectionSize);

    }

    private List<CandidateSolution> initialisation() {

	    /* initialize population list of CandidateSolutions */
        List<CandidateSolution> populationTemp = new ArrayList<CandidateSolution>(populationSize);

	    /* create a populationSize amount of random CandidateSolutions (routes) */
        for (int i = 0; i < populationSize; i++) {
            CandidateSolution candidateSolution = new CandidateSolution(
                    TSPUtils.getBaseCity(), TSPUtils.getRandomizedCities());
            populationTemp.add(candidateSolution);
        }

        return populationTemp;
    }

    /**
     * Stops the algorithm
     */
    public void stopAlgorithm() {
        running = false;
    }

    /**
     * Returns the best route of the current population
     */
    public CandidateSolution getCurrentBest() throws IllegalStateException {
        return population.isEmpty() ? null : population.get(0);
    }

    /**
     * Returns whether the algorithm is still running
     */
    public boolean isStillRunning() {
        return running;
    }

}
