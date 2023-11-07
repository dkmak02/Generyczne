import java.io.*;
import java.util.StringTokenizer;

public class TinyGP extends Constants {
    double[] fitness;
    char[][] pop;
    double[] x = new double[FSET_START];
    char[] program;
    int programCounter;
    int variableNumber, fitnessCases, randomNumber;
    double fBestPop = 0.0, fAvgPop = 0.0;
    long seed;
    double avgLen;
    final int POP_SIZE = 25000, DEPTH = 5, GENERATIONS = 40, TOURNAMENT_SIZE = 2;
    public final double PMUT_PER_NODE = 0.05, CROSSOVER_PROB = 0.9;
    double[][] targets;

    private FileWriter generationDataWriter;

    double run() {
        char primitive = program[programCounter++];
        if (primitive < FSET_START)
            return x[primitive];
        switch (primitive) {
            case ADD:
                return run() + run();
            case SUB:
                return run() - run();
            case MUL:
                return run() * run();
            case DIV: {
                double num = run(), den = run();
                if (Math.abs(den) <= 0.001)
                    return num;
                else
                    return num / den;
            }
            case SIN:
                return Math.sin(run());
            case COS:
                return Math.cos(run());
        }
        return 0.0;
    }

    int traverse(char[] buffer, int bufferCount) {
        if (buffer[bufferCount] < FSET_START)
            return ++bufferCount;

        switch (buffer[bufferCount]) {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case SIN:
            case COS:
                return traverse(buffer, traverse(buffer, ++bufferCount));
        }
        return 0;
    }

    void setupFitness() {
        String fileName = "..\\TinyGP-Java-master\\problem.dat";
        try {
            int i, j;
            String line;

            BufferedReader in = new BufferedReader(new FileReader(fileName));
            line = in.readLine();
            StringTokenizer tokens = new StringTokenizer(line);
            variableNumber = Integer.parseInt(tokens.nextToken().trim());
            randomNumber = Integer.parseInt(tokens.nextToken().trim());
            minRandom = Double.parseDouble(tokens.nextToken().trim());
            maxRandom = Double.parseDouble(tokens.nextToken().trim());
            fitnessCases = Integer.parseInt(tokens.nextToken().trim());
            targets = new double[fitnessCases][variableNumber + 1];
            if (variableNumber + randomNumber >= FSET_START)
                System.out.println("Too many variables and constants");

            for (i = 0; i < fitnessCases; i++) {
                line = in.readLine();
                tokens = new StringTokenizer(line);
                for (j = 0; j <= variableNumber; j++) {
                    targets[i][j] = Double.parseDouble(tokens.nextToken().trim());
                }
            }
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Please provide a data file");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("ERROR: Incorrect data format");
            System.exit(0);
        }
    }

    double fitnessFunction(char[] prog) {
        int i = 0, len;
        double result, fit = 0.0;

        len = traverse(prog, 0);
        for (i = 0; i < fitnessCases; i++) {
            for (int j = 0; j < variableNumber; j++)
                x[j] = targets[i][j];
            program = prog;
            programCounter = 0;
            result = run();
            fit += Math.abs(result - targets[i][variableNumber]);
        }
        return -fit;
    }

    int grow(char[] buffer, int pos, int max, int depth) {
        char prim = (char) rd.nextInt(2);
        int oneChild;

        if (pos >= max)
            return -1;

        if (pos == 0)
            prim = 1;

        if (prim == 0 || depth == 0) {
            prim = (char) rd.nextInt(variableNumber + randomNumber);
            buffer[pos] = prim;
            return pos + 1;
        } else {
            prim = (char) (rd.nextInt(FSET_END - FSET_START + 1) + FSET_START);
            switch (prim) {
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case SIN:
                case COS:
                    buffer[pos] = prim;
                    oneChild = grow(buffer, pos + 1, max, depth - 1);
                    if (oneChild < 0)
                        return -1;
                    return grow(buffer, oneChild, max, depth - 1);
            }
        }
        return 0;
    }

    int printIndividual(char[] buffer, int bufferCounter) {
        int a1 = 0, a2;
        if (buffer[bufferCounter] < FSET_START) {
            if (buffer[bufferCounter] < variableNumber)
                System.out.print("X" + (buffer[bufferCounter] + 1) + " ");
            else
                System.out.print(x[buffer[bufferCounter]]);
            return ++bufferCounter;
        }
        switch (buffer[bufferCounter]) {
            case ADD:
                System.out.print("(");
                a1 = printIndividual(buffer, ++bufferCounter);
                System.out.print(" + ");
                break;
            case SUB:
                System.out.print("(");
                a1 = printIndividual(buffer, ++bufferCounter);
                System.out.print(" - ");
                break;
            case MUL:
                System.out.print("(");
                a1 = printIndividual(buffer, ++bufferCounter);
                System.out.print(" * ");
                break;
            case DIV:
                System.out.print("(");
                a1 = printIndividual(buffer, ++bufferCounter);
                System.out.print(" / ");
                break;
            case SIN:
                System.out.print("sin(");
                a1 = printIndividual(buffer, ++bufferCounter);
                System.out.print(")");
                return a1;
            case COS:
                System.out.print("cos(");
                a1 = printIndividual(buffer, ++bufferCounter);
                System.out.print(")");
                return a1;
        }
        a2 = printIndividual(buffer, a1);
        System.out.print(")");
        return a2;
    }

    int printIndividual(char[] buffer, int bufferCounter, FileWriter output) {
        int a1 = 0, a2;
        try {
            if (buffer[bufferCounter] < FSET_START) {
                if (buffer[bufferCounter] < variableNumber) {
                    output.write("X" + (buffer[bufferCounter] + 1) + " ");
                } else {
                    output.write(Double.toString(x[buffer[bufferCounter]]));
                }
                return ++bufferCounter;
            }
            switch (buffer[bufferCounter]) {
                case ADD:
                    output.write("(");
                    a1 = printIndividual(buffer, ++bufferCounter, output);
                    output.write(" + ");
                    break;
                case SUB:
                    output.write("(");
                    a1 = printIndividual(buffer, ++bufferCounter, output);
                    output.write(" - ");
                    break;
                case MUL:
                    output.write("(");
                    a1 = printIndividual(buffer, ++bufferCounter, output);
                    output.write(" * ");
                    break;
                case DIV:
                    output.write("(");
                    a1 = printIndividual(buffer, ++bufferCounter, output);
                    output.write(" / ");
                    break;
                case SIN:
                    output.write("sin(");
                    a1 = printIndividual(buffer, ++bufferCounter, output);
                    output.write(")");
                    return a1;
                case COS:
                    output.write("cos(");
                    a1 = printIndividual(buffer, ++bufferCounter, output);
                    output.write(")");
                    return a1;
            }
            a2 = printIndividual(buffer, a1, output);
            output.write(")");
            return a2;
        } catch (IOException e) {
            e.printStackTrace();
            return bufferCounter;
        }
    }

    static char[] buffer = new char[MAX_LEN];

    char[] createRandomIndividual(int depth) {
        char[] ind;
        int len;

        len = grow(buffer, 0, MAX_LEN, depth);

        while (len < 0)
            len = grow(buffer, 0, MAX_LEN, depth);

        ind = new char[len];

        System.arraycopy(buffer, 0, ind, 0, len);
        return ind;
    }

    char[][] createRandomPopulation(int n, int depth, double[] fitness) {
        char[][] population = new char[n][];
        int i;

        for (i = 0; i < n; i++) {
            population[i] = createRandomIndividual(depth);
            fitness[i] = fitnessFunction(population[i]);
        }
        return population;
    }

    void statistics(double[] fitness, char[][] population, int generation) {
        int i;
        double totalFitness = 0.0;
        int best = 0;
        int nodeCount = 0;
        fBestPop = fitness[0];
        fAvgPop = 0.0;

        for (i = 0; i < POP_SIZE; i++) {
            int currentNodeCount = traverse(population[i], 0);
            nodeCount += currentNodeCount;
            totalFitness += fitness[i];

            if (fitness[i] > fBestPop) {
                best = i;
                fBestPop = fitness[i];
            }
        }

        avgLen = (double) nodeCount / POP_SIZE;
        fAvgPop = -totalFitness / POP_SIZE;

        System.out.print("Generation=" + generation + " Avg Fitness=" + fAvgPop + " Best Fitness=" + (-fBestPop) + " Avg Size=" + avgLen + "\nBest Individual: ");
        printIndividual(population[best], 0);
        System.out.print("\n");
        System.out.flush();

        saveGenerationData(generation, fAvgPop, -fBestPop);
    }

    int tournamentSelection(double[] fitness, int tournamentSize) {
        int best = rd.nextInt(POP_SIZE), i, competitor;
        double fBest = -1.0e34;

        for (i = 0; i < tournamentSize; i++) {
            competitor = rd.nextInt(POP_SIZE);
            if (fitness[competitor] > fBest) {
                fBest = fitness[competitor];
                best = competitor;
            }
        }
        return best;
    }

    int negativeTournamentSelection(double[] fitness, int tournamentSize) {
        int worst = rd.nextInt(POP_SIZE), i, competitor;
        double fWorst = 1e34;

        for (i = 0; i < tournamentSize; i++) {
            competitor = rd.nextInt(POP_SIZE);
            if (fitness[competitor] < fWorst) {
                fWorst = fitness[competitor];
                worst = competitor;
            }
        }
        return worst;
    }

    char[] crossover(char[] parent1, char[] parent2) {
        int xo1Start, xo1End, xo2Start, xo2End;
        char[] offspring;
        int len1 = traverse(parent1, 0);
        int len2 = traverse(parent2, 0);
        int lenOff;

        xo1Start = rd.nextInt(len1);
        xo1End = traverse(parent1, xo1Start);

        xo2Start = rd.nextInt(len2);
        xo2End = traverse(parent2, xo2Start);

        lenOff = xo1Start + (xo2End - xo2Start) + (len1 - xo1End);

        offspring = new char[lenOff];

        System.arraycopy(parent1, 0, offspring, 0, xo1Start);
        System.arraycopy(parent2, xo2Start, offspring, xo1Start, (xo2End - xo2Start));
        System.arraycopy(parent1, xo1End, offspring, xo1Start + (xo2End - xo2Start), (len1 - xo1End));

        return offspring;
    }

    char[] mutation(char[] parent, double pMut) {
        int len = traverse(parent, 0), i;
        int mutSite;
        char[] parentCopy = new char[len];

        System.arraycopy(parent, 0, parentCopy, 0, len);
        for (i = 0; i < len; i++) {
            if (rd.nextDouble() < pMut) {
                mutSite = i;
                if (parentCopy[mutSite] < FSET_START)
                    parentCopy[mutSite] = (char) rd.nextInt(variableNumber + randomNumber);
                else
                    switch (parentCopy[mutSite]) {
                        case ADD:
                        case SUB:
                        case MUL:
                        case DIV:
                        case SIN:
                        case COS:
                            parentCopy[mutSite] = (char) (rd.nextInt(FSET_END - FSET_START + 1) + FSET_START);
                    }
            }
        }
        return parentCopy;
    }

    void saveGenerationData(int generation, double avgFitness, double bestFitness) {
        try {
            generationDataWriter.write(generation + "," + (avgFitness) + "," + (bestFitness) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TinyGP(String fileName, long s) {
        fitness = new double[POP_SIZE];
        seed = s;
        if (seed >= 0)
            rd.setSeed(seed);
        setupFitness();
        for (int i = 0; i < FSET_START; i++)
            x[i] = (maxRandom - minRandom) * rd.nextDouble() + minRandom;
        pop = createRandomPopulation(POP_SIZE, DEPTH, fitness);

        try {
            generationDataWriter = new FileWriter("generation_data.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printParams() {
        System.out.print("-- TINY GP (Java version) --\n");
        System.out.print("SEED=" + seed + "\nMAX_LEN=" + MAX_LEN +
                "\nPOPSIZE=" + POP_SIZE + "\nDEPTH=" + DEPTH +
                "\nCROSSOVER_PROB=" + CROSSOVER_PROB +
                "\nPMUT_PER_NODE=" + PMUT_PER_NODE +
                "\nMIN_RANDOM=" + minRandom +
                "\nMAX_RANDOM=" + maxRandom +
                "\nGENERATIONS=" + GENERATIONS +
                "\nTSIZE=" + TOURNAMENT_SIZE +
                "\n----------------------------------\n");
    }

    public void evolve() {
        int gen = 0, indivs, offspring, parent1, parent2, parent;
        double newfit;
        char[] newind;
        printParams();
        statistics(fitness, pop, 0);
        try {
            generationDataWriter.write("Generation, Avg Fitness, Best Fitness, Avg Size\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (gen < GENERATIONS) {
            if (fBestPop > 1) {
                System.out.print("PROBLEM SOLVED\n");
                statistics(fitness, pop, gen);
                try {
                    generationDataWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                char[] bestIndividual = pop[0];
                double bestFitness = fitness[0];
                for (int i = 1; i < POP_SIZE; i++) {
                    if (fitness[i] > bestFitness) {
                        bestFitness = fitness[i];
                        bestIndividual = pop[i];
                    }
                }
                try {
                    FileWriter bestIndividualWriter = new FileWriter("best.txt");
                    printIndividual(bestIndividual, 0, bestIndividualWriter);
                    bestIndividualWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
            for (indivs = 0; indivs < POP_SIZE; indivs++) {
                if (rd.nextDouble() < CROSSOVER_PROB) {
                    parent1 = tournamentSelection(fitness, TOURNAMENT_SIZE);
                    parent2 = tournamentSelection(fitness, TOURNAMENT_SIZE);
                    newind = crossover(pop[parent1], pop[parent2]);
                } else {
                    parent = tournamentSelection(fitness, TOURNAMENT_SIZE);
                    newind = mutation(pop[parent], PMUT_PER_NODE);
                }
                newfit = fitnessFunction(newind);
                offspring = negativeTournamentSelection(fitness, TOURNAMENT_SIZE);
                pop[offspring] = newind;
                fitness[offspring] = newfit;
            }
            statistics(fitness, pop, gen);

            gen++;
        }
        try {
            generationDataWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        char[] bestIndividual = pop[0];
        double bestFitness = fitness[0];
        for (int i = 1; i < POP_SIZE; i++) {
            if (fitness[i] > bestFitness) {
                bestFitness = fitness[i];
                bestIndividual = pop[i];
            }
        }
        try {
            FileWriter bestIndividualWriter = new FileWriter("best.txt");
            printIndividual(bestIndividual, 0, bestIndividualWriter);
            bestIndividualWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print("PROBLEM *NOT* SOLVED\n");
        System.exit(1);
    }
}
