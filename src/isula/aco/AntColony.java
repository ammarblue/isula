package isula.aco;

import isula.aco.exception.ConfigurationException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A group of ants. As an abstract type, you need to define how to build
 * individual ants through the createAnt() method.
 * 
 * @author Carlos G. Gavidia
 * 
 * @param <C>
 *          Class for components of a solution.
 */
public abstract class AntColony<C, E extends Environment> {

  private static Logger logger = Logger.getLogger(AntColony.class.getName());

  private int numberOfAnts;
  private List<Ant<C, E>> hive = new ArrayList<Ant<C, E>>();

  /**
   * Creates a colony of ants
   * 
   * @param numberOfAnts
   *          Number of ants in the colony.
   */
  public AntColony(int numberOfAnts) {
    this.numberOfAnts = numberOfAnts;

    logger.info("Number of Ants in Colony: " + numberOfAnts);
  }

  /**
   * Initialization code for the colony. The main responsibility is Ant
   * instantiation.
   * 
   */
  public void buildColony(E environment) {
    for (int j = 0; j < numberOfAnts; j++) {
      hive.add(this.createAnt(environment));
    }
  }

  protected abstract Ant<C, E> createAnt(E environment);

  /**
   * Returns the ant with the best performance so far.
   * 
   * @param environment
   *          Environment where the Ants are building solutions.
   * @return Best performing Ant.
   */
  public Ant<C, E> getBestPerformingAnt(E environment) {
    Ant<C, E> bestAnt = hive.get(0);

    for (Ant<C, E> ant : hive) {

      if (ant.getSolutionQuality(environment) < bestAnt
          .getSolutionQuality(environment)) {
        bestAnt = ant;
      }
    }

    return bestAnt;
  }

  /**
   * Returns a List of all the ants in the colony.
   * 
   * @return List of Ants.
   */
  public List<Ant<C, E>> getHive() {
    return hive;
  }

  /**
   * Clears solution build for every Ant in the colony.
   */
  public void clearAntSolutions() {
    logger.log(Level.FINE, "CLEARING ANT SOLUTIONS");

    for (Ant<C, E> ant : hive) {
      ant.setCurrentIndex(0);
      ant.clear();
    }

  }

  /**
   * Puts every ant in the colony to build a solution.
   * 
   * @param environment
   *          Environment that represents the problem.
   * @param configurationProvider
   *          Configuration provider.
   */
  public void buildSolutions(E environment,
      ConfigurationProvider configurationProvider) {
    logger.log(Level.FINE, "BUILDING ANT SOLUTIONS");

    int antCounter = 0;

    if (hive.size() == 0) {
      throw new ConfigurationException(
          "Your colony is empty: You have no ants to solve the problem. "
              + "Have you called the buildColony() method?");
    }

    for (Ant<C, E> ant : hive) {
      logger.info("Current ant: " + antCounter);

      while (!ant.isSolutionReady(environment)) {
        ant.selectNextNode(environment, configurationProvider);
      }

      ant.doAfterSolutionIsReady(environment, configurationProvider);

      logger.log(Level.FINE,
          "Solution is ready > Quality: " + ant.getSolutionQuality(environment)
              + ", Solution: " + ant.getSolutionAsString());

      antCounter++;
    }
  }

  /**
   * Adds a list of policies to every Ant in the Colony.
   * 
   * @param antPolicies
   *          List of policies.
   */
  @SafeVarargs
  public void addAntPolicies(AntPolicy<C, E>... antPolicies) {
    List<Ant<C, E>> hive = getHive();
    for (Ant<C, E> ant : hive) {
      for (AntPolicy<C, E> antPolicy : antPolicies) {
        ant.addPolicy(antPolicy);
      }
    }
  }

  public int getNumberOfAnts() {
    return numberOfAnts;
  }

  public void setNumberOfAnts(int numberOfAnts) {
    this.numberOfAnts = numberOfAnts;
  }

}
