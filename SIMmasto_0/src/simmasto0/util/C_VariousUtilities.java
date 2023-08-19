package simmasto0.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;

import com.vividsolutions.jts.geom.Coordinate;

import cern.jet.random.engine.RandomEngine;
import data.C_Parameters;
import data.constants.I_ConstantString;
import repast.simphony.random.RandomHelper;
import simmasto0.protocol.A_Protocol;
import thing.A_Animal;
import thing.A_VisibleAgent;
import thing.I_SituatedThing;
import thing.dna.C_Chromosome;
import thing.dna.C_ChromosomePair;
import thing.dna.C_Gene;
import thing.dna.I_DiploidGenome;
import thing.dna.I_MappedDna;
import thing.dna.variator.C_GeneConstraint;
import thing.dna.variator.C_GeneMutatorDouble;
import thing.dna.variator.C_GeneMutatorSet;
import thing.dna.variator.C_RecombinatorMapGenome;
import thing.dna.variator.C_RecombinatorOnePt;
import thing.dna.variator.I_Recombinator;
import thing.ground.A_Container;
import thing.ground.C_SoilCell;

public class C_VariousUtilities implements I_ConstantString {
	//
	// CONSTRUCTOR
	//
	public C_VariousUtilities() {}
	//
	// METHODS
	//
	/** Test preferredSize (swing) */
	public static void main0(String string) {
		JFrame f = new JFrame("Label Demo");
		f.setLayout(new FlowLayout());
		f.setSize(300, 360);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel label = new JLabel("asdf");
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		label.setBorder(border);
		label.setPreferredSize(new Dimension(150, 100));
		f.add(label);

		f.setVisible(true);
	}
	/** Example on how to use swing boxlayout */
	public static void BoxLayoutoo() {
		JFrame BoxLayoutFoo = new JFrame();
		// swap the comments below
		// BoxLayoutFoo.setLayout(new BoxLayout(BoxLayoutFoo, BoxLayout.LINE_AXIS)); // comment out this line
		BoxLayoutFoo.setLayout(new BoxLayout(BoxLayoutFoo.getContentPane(), BoxLayout.LINE_AXIS)); // uncomment this line
		BoxLayoutFoo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BoxLayoutFoo.pack();
		BoxLayoutFoo.setVisible(true);
	}
	/** utility to perform the comparison of decimals values Author J.Le Fur august 2012 */
	public static boolean compareDecimals(double n1, double n2) {
		return truncate(n1) == truncate(n2);
	}
	/** Test of the method used in C_GenomeEucaryote
	 * @see I_DiploidGenome#isHybrid */
	public void testCompareDecimals(String args[]) {
		double x = 4.29, y = 4.3, z = 5.30, w = 4.31;
		System.out.println(compareDecimals(x, y));
		System.out.println(compareDecimals(x, z));
		System.out.println(compareDecimals(x, w));
		System.out.println(compareDecimals(y, z));
		System.out.println(compareDecimals(y, w));
	}
	/** utility to extract the decimals values Author J.Le Fur august 2012 x is supposed to have a maximum of two decimal
	 * digits */
	private static double truncate(double x) {
		long y = (long) (x * 1000.);
		y = y - ((int) x) * 1000;
		return y;
	}

	public static void testRegisterGenerator() {
		final int BINARY_RANDOM_SEED = 654879511;
		RandomEngine randomGeneratorForBinarySelector;
		randomGeneratorForBinarySelector = RandomHelper.registerGenerator("binary_seed", BINARY_RANDOM_SEED);
		for (int i = 0; i < 30; i++) {
			System.out.print((int) (randomGeneratorForBinarySelector.nextDouble() * 2));
			System.out.print(" / nextdouble: ");
			System.out.print(randomGeneratorForBinarySelector.nextDouble() * 2);
			System.out.print(" / nextint: ");
			System.out.println(randomGeneratorForBinarySelector.nextInt());
		}
	}
	/** Test the chromosome function (was formerly the main of C_Chromosome) author Kyle Wagner circa 2002, rev. JLF 2012
	 * @see C_Chromosome */
	public void testChromosome(String[] args) {
		System.out.println("Testing chromosome");
		Random randGen = new Random();
		C_Chromosome one_chromosome = new C_Chromosome(4, new C_RecombinatorOnePt());
		// crée 4 gènes avec des valeurs (gène <=> allèle) aléatoires aux loci 1 à 4.
		for (int i = 1; i <= 4; i++) {
			double mapLoc = i * 2.5;
			C_Gene dblGene = new C_Gene(new Double(new Random().nextDouble()), mapLoc, new C_GeneMutatorDouble(),
					one_chromosome.getMyId(), new C_GeneConstraint(-10, 10));
			one_chromosome.setGeneAtLocus(i, dblGene);
		}
		System.out.println("chromosome:         " + one_chromosome);
		testMapLocs(one_chromosome);
		one_chromosome.mutate(new Double(0.5));
		System.out.println("mutated chromosome: " + one_chromosome);
		testMapLocs(one_chromosome);
		for (int i = 0; i < 4; i++) {
			System.out.println("getGene(" + i + "): " + one_chromosome.getGene(i));
			System.out.println("  getLocusAllele(" + i + "): " + one_chromosome.getLocusAllele(i));
		}
		// Test randomize();
		one_chromosome.randomize();
		System.out.println("...randomize(): " + one_chromosome);
		testMapLocs(one_chromosome);
		System.out.println("getAlleles(): " + one_chromosome.getAlleles());
		// Replicate w/o mutation
		C_Chromosome gCopy = (C_Chromosome) one_chromosome.replicate(new Double(0));
		System.out.println("original: " + one_chromosome);
		System.out.println("copy:     " + gCopy);
		gCopy.mutate(new Double(1));
		System.out.println("original: " + one_chromosome);
		System.out.println("mut copy: " + gCopy);
		testMapLocs(gCopy);
		// Test out crossover
		I_Recombinator onePt = new C_RecombinatorOnePt();
		System.out.println("\nOne Pt Crossover:");
		testCrossover(onePt, one_chromosome, gCopy);
		I_Recombinator multiPt = new C_RecombinatorOnePt();
		System.out.println("\nMulti Pt Crossover:");
		testCrossover(multiPt, one_chromosome, gCopy);
		I_Recombinator mapXover = new C_RecombinatorMapGenome();
		System.out.println("\nMap Genome Crossover:");
		testCrossover(mapXover, one_chromosome, gCopy);
		System.out.println("\nCrossover and Mating");
		System.out.println("parent1: " + one_chromosome);
		System.out.println("parent2: " + gCopy);
		System.out.println("crossover(): " + one_chromosome.crossover(gCopy));
		C_Chromosome mate = (C_Chromosome) one_chromosome.mate(new Double(0.75), gCopy);
		System.out.println("mate(): " + mate);
		testMapLocs(mate);
		int numGenes = 20;
		double mapLen = numGenes * 2.5;
		List<Integer> intAlleles = new ArrayList<Integer>(10);
		for (int i = 0; i < 10; i++)
			intAlleles.add(new Integer(i));
		C_GeneMutatorSet setGeneMut = new C_GeneMutatorSet(intAlleles);
		C_Chromosome genome3 = new C_Chromosome(mapLen, numGenes, mapXover);
		double mapLoc = 0;
		for (int i = 1; i <= numGenes; i++) {
			mapLoc += randGen.nextDouble() * 2.5;
			C_Gene dblGene = new C_Gene(new Integer((int) randGen.nextDouble() * 10), mapLoc, setGeneMut, one_chromosome
					.getMyId());
			genome3.setGeneAtLocus(i, dblGene);
		}
		C_Chromosome genome3copy = (C_Chromosome) genome3.replicate(new Double(0));
		genome3copy.mutate(new Double(1));
		System.out.println("genome3:         " + genome3);
		System.out.println("genome3copy:     " + genome3copy);
		testMapLocs(genome3);
		testCrossover(mapXover, genome3, genome3copy);
		// Test copySegment()
		I_MappedDna copiedSegment1 = (I_MappedDna) one_chromosome.copySegment(0, 1);
		I_MappedDna copiedSegment2 = (I_MappedDna) one_chromosome.copySegment(1, 3);
		I_MappedDna copiedSegment3 = (I_MappedDna) one_chromosome.copySegment(3, 4);
		System.out.println("\ncopySegment()");
		System.out.println("genome: " + one_chromosome);
		System.out.println("genome.copySegment(0,1):" + copiedSegment1);
		testMapLocs(copiedSegment1);
		System.out.println("genome.copySegment(1,3):" + copiedSegment2);
		testMapLocs(copiedSegment2);
		System.out.println("genome.copySegment(3,4):" + copiedSegment3);
		testMapLocs(copiedSegment3);
	}
	private static void testMapLocs(I_MappedDna genome) {
		double mapLoc = 5.0;
		System.out.println("genome.getMapLength():         " + genome.getMapLength());
		for (int i = 0; i < genome.getNumLoci(); i++) {
			C_Gene gene = ((C_Chromosome) genome).getGene(i);
			System.out.print("  getGene(" + i + "): " + gene);
			System.out.println("  mapLoc: " + gene.getMapLoc());
			System.out.println("  mapLoc(" + mapLoc + ") allele: " + genome.getLocusAllele(mapLoc));
		}
	}
	private static void testCrossover(I_Recombinator r, C_Chromosome parent1, C_Chromosome parent2) {
		System.out.println("parent1:   " + parent1);
		System.out.println("parent2:   " + parent2);
		C_ChromosomePair child = (C_ChromosomePair) r.crossover(parent1, parent2);
		System.out.println("offspring: " + child);
		testMapLocs(child);

		System.out.println("\nMutating child (100%), parents shouldn't change:");
		child.mutate(new Double(1));
		System.out.println("parent1:   " + parent1);
		System.out.println("parent2:   " + parent2);
		System.out.println("offspring: " + child);
	}

	/** return classes names with or without packages Author http://www.rgagnon.com/javadetails/java-0389.html, ver. 09.2011 */
	public static void testClassNames() {
		System.out.println(getShortClassName(java.awt.Frame.class));
		System.out.println(getFullClassName(java.awt.Frame.class));
		System.out.println(getPackageName(java.awt.Frame.class));
		System.out.println("----");
		System.out.println(getShortClassName(C_VariousUtilities.class));
		System.out.println(getFullClassName(C_VariousUtilities.class));
		System.out.println(getPackageName(C_VariousUtilities.class));
		System.out.println("----");
		java.util.Calendar cal = java.util.Calendar.getInstance();
		System.out.println(getShortClassName(cal.getClass()));
		System.out.println(getFullClassName(cal.getClass()));
		System.out.println(getPackageName(cal.getClass()));
	}
	/** returns the class (without the package if any) */
	public static String getShortClassName(Class<?> c) {
		String FQClassName = c.getName();
		int firstChar;
		firstChar = FQClassName.lastIndexOf('.') + 1;
		if (firstChar > 0) {
			FQClassName = FQClassName.substring(firstChar);
		}
		return FQClassName;
	}

	/** returns package and class name */
	public static String getFullClassName(Class<?> c) {
		return c.getName();
	}

	/** returns the package without the classname, empty string if there is no package */
	public static String getPackageName(Class<?> c) {
		String fullyQualifiedName = c.getName();
		int lastDot = fullyQualifiedName.lastIndexOf('.');
		if (lastDot == -1) { return ""; }
		return fullyQualifiedName.substring(0, lastDot);
	}

	/** Compute distance to targeting thing */
	public static double distance_Umeter(Coordinate firstPoint, Coordinate secondPoint) {
		return Math.sqrt((firstPoint.x - secondPoint.x) * (firstPoint.x - secondPoint.x) + (firstPoint.y
				- secondPoint.y) * (firstPoint.y - secondPoint.y));
	}
	/** Compute distance to targeting thing */
	public static double distance_Umeter(I_SituatedThing first, I_SituatedThing second) {
		return distance_Umeter(first.getCoordinate_Umeter(), second.getCoordinate_Umeter());
	}
	/** Compute and return a movement vector between two point according to a verification distance Le déplacement est suivant un
	 * vecteur de coordonnées (vectCoordX, vectCoordY) ie fisrtPoint - secondPoint (sens), de longeur vectNorm =
	 * racine(vectCoordX², vectCoordY²) (norme) et faisant un angle alpha par rapport à l'abscisse (direction) si vectCoordX == 0,
	 * alpha = +PI/2 ou -PI/2 ça dépend du signe de vectCoordY sinon alpha = atan(vectCoordY/vectCoordX) Ainsi nous avons la
	 * direction et le sens du déplacement. Et donc à partir de la distance à parcourir, on peut calculer exactement le vecteur de
	 * déplacement */
	public static Coordinate computeVectorBetweenTwoPoints(Coordinate A_Point, Coordinate B_Point,
			double distanceToCover_Umeter) {
		double alpha, signeX;
		double vectorAB_X = B_Point.x - A_Point.x;
		double vectorAB_Y = B_Point.y - A_Point.y;
		if (vectorAB_X == 0) {
			signeX = 1.0; // positif
			alpha = Math.signum(vectorAB_Y) * Math.PI / 2;
		}
		else {
			signeX = Math.signum(vectorAB_X);
			alpha = Math.atan(vectorAB_Y / vectorAB_X);
		}
		return new Coordinate(signeX * distanceToCover_Umeter * Math.cos(alpha), signeX * distanceToCover_Umeter * Math
				.sin(alpha));
	}
	/** Compute sum of two vectors */
	public static Coordinate sum(Coordinate vector1, Coordinate vector2) {
		return new Coordinate((vector1.x + vector2.x), (vector1.y + vector2.y));
	}

	/** Compute the subtraction of two vectors */
	public static Coordinate substract(Coordinate vector1, Coordinate vector2) {
		return new Coordinate((vector1.x - vector2.x), (vector1.y - vector2.y));
	}

	/** Verify if two vectors are collinear */
	public static Boolean isCollinearVector(Coordinate vector1, Coordinate vector2) {
		return (vector1.x * vector2.y == vector1.y * vector2.x);
	}
	/** Use perceived things and select destinations to verify if obstacle exist between agent position and the target
	 * @version JLF 02.2020 */
	public static I_SituatedThing checkObstacleBefore(A_Animal animal, I_SituatedThing target) {
		Coordinate animalCoord = animal.getCoordinate_Umeter();
		Coordinate targetCoord = target.getCoordinate_Umeter();
		double signX = Math.signum(animalCoord.x - targetCoord.x);
		double signY = Math.signum(animalCoord.y - targetCoord.y);
		double gridWidth = A_Animal.myLandscape.getDimension_Ucell().getWidth();
		double gridHeight = A_Animal.myLandscape.getDimension_Ucell().getHeight();
		int ccCol = animal.getCurrentSoilCell().retrieveColNo();
		int ccLine = animal.getCurrentSoilCell().retrieveLineNo();
		C_SoilCell oldcc = null, cc = (C_SoilCell) animal.getCurrentSoilCell();// cc means containerToCheck
		while (cc != target) {
			if (ccCol != ((A_Container) target).retrieveColNo()) ccCol = (int) (cc.retrieveColNo() - signY);
			if (ccLine != ((A_Container) target).retrieveLineNo()) ccLine = (int) (cc.retrieveLineNo() - signX);
			if (ccLine < gridWidth && ccCol < gridHeight && ccLine >= 0 && ccCol >= 0) // inside the grid
			{
				oldcc = cc;
				cc = (C_SoilCell) A_VisibleAgent.myLandscape.getGrid()[ccLine][ccCol];
				if (animal.isSightObstacle(cc)) return cc;
			}
			else {
				A_Protocol.event("C_VariousUtilities.checkObstacleBefore()", "Outside grid: " + animal + ", back to "
						+ oldcc.toString(), isError);
				return oldcc;
			}
		}
		return null;
	}

	// TODO MS&JLF 2020.04 refactor the procedure name
	/** Use perceived things and select destinations to verify if obstacle exist between agent position and the target
	 * @version MS 2019.03, rev JLF 02.2020 */
	public static I_SituatedThing checkObstacleBefore0(A_Animal animal, I_SituatedThing target) {
		I_SituatedThing cellToCheck = null;
		Coordinate currentCheckPoint = animal.getCoordinate_Umeter();
		double oneStep_Umeter = C_Parameters.CELL_WIDTH_Umeter;
		double distanceToTarget_Umeter = 0.;
		Coordinate moveVector = computeVectorBetweenTwoPoints(currentCheckPoint, target.getCoordinate_Umeter(),
				oneStep_Umeter);
		do {
			distanceToTarget_Umeter = target.getCoordinate_Umeter().distance(currentCheckPoint);
			currentCheckPoint.x += moveVector.x;
			currentCheckPoint.y += moveVector.y;
			if (A_VisibleAgent.myLandscape.isPointInGrid(currentCheckPoint))
				cellToCheck = A_VisibleAgent.myLandscape
						.getGrid()[(int) currentCheckPoint.x][(int) currentCheckPoint.y];
		} while ((distanceToTarget_Umeter > oneStep_Umeter) && !animal.isSightObstacle(cellToCheck) //
				&& (!target.equals(cellToCheck)));
		if ((distanceToTarget_Umeter <= oneStep_Umeter) || target.equals(cellToCheck) //
				|| (animal.equals(cellToCheck))) return null;
		return cellToCheck;
	}
	public static void main(String[] args) {
		testClassNames();
		BoxLayoutoo();
		main0("goo");
		testRegisterGenerator();
	}
}
