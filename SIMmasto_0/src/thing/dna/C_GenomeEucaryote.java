/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.dna;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.constants.I_ConstantNumeric;
import data.constants.I_ConstantString;
import presentation.epiphyte.C_InspectorHybrid;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;

/** Defines a diploid and sexed genome. It owns a sexual chromosome pair with a sex locus (and possibly room for sexual behaviour
 * coding). <br>
 * A genome should be able to own several C_ChromosomePairs. A genome may be assimilated to an egg.
 * @see thing.C_Egg
 * @author Jean Le Fur, lefur@ird.fr 2011 adapted from PulseRateGenome kyle wagner, elyk@acm.org; Aurore Comte 2012 Version 2,
 *         01.2011/09.2011/01.2012/03.2012 */

public abstract class C_GenomeEucaryote extends A_GenomeLuca implements I_DiploidGenome, I_ConstantNumeric, I_ConstantString {
	//
	// FIELDS
	//
	// check the (past)use of this :
	// public static final I_gene_pair_evaluator ALLELE_EVALUATOR = new C_GenePairEvaluatorSum();

	// position of chromosomes in the genome.
	public static final int MICROSAT_INDEX = 0;
	private static final int GONOSOME_INDEX = 1, BIVALENTS_INDEX = 2;
	public static final int LETHAL_ALLELE = 3697;// TODO number in source OK, lethal allele
	protected C_XsomePairMicrosat microsatXsomesPair;
	protected C_XsomePairSexual gonosome;
	protected ArrayList<C_ChromosomePair> bivalents; // i.e., autosomes pairs
	protected static C_InspectorHybrid hybridInspector = null;
	protected boolean isHybrid = false;
	protected Map<String, Double> alleles = new HashMap<String, Double>();

	//
	// CONSTRUCTORS
	//
	/** creates a genome randomly male or female */
	public C_GenomeEucaryote() {
		super();
		microsatXsomesPair = new C_XsomePairMicrosat(NB_MICROSAT_GENES);
		int gender = (int) (C_ContextCreator.randomGeneratorForGameteAndSexSelection.nextDouble() * 2);
		gonosome = new C_XsomePairSexual(gender);
		bivalents = new ArrayList<C_ChromosomePair>();
	}

	/** Returns a new GenomeEucaryote. This method is used for mate.
	 * @param microsatXsomes
	 * @param sexualXsomes
	 * @param otherXsomePairs */
	public C_GenomeEucaryote(C_XsomePairMicrosat microsatXsomes, C_XsomePairSexual sexualXsomes,
			ArrayList<C_ChromosomePair> otherXsomePairs) {
		super();
		this.microsatXsomesPair = microsatXsomes;
		this.gonosome = sexualXsomes;
		this.bivalents = otherXsomePairs;
		this.isHybrid = isHybrid(bivalents);
	}
	//
	// METHODS
	//
	/** Simulates meiosis with crossing over. Meiosis 2 is not formalized. It would necessitate to duplicate (using the java
	 * clone()) the chromosomes pairs Authors J.Le Fur, A. Comte - 01.2012 */
	public ArrayList<C_Chromosome> makeGametes() {
		// Initialization of a new gamete
		ArrayList<C_Chromosome> gametes = new ArrayList<C_Chromosome>();
		// Cross the two chromosomes of the microsat pair. Then put randomly one of the two newly crossed microsatXsome into the
		// gamete.
		// This is also done for the gonosome.
		int i = (int) (C_ContextCreator.randomGeneratorForGameteAndSexSelection.nextDouble() * 2);
		gametes.add(((C_ChromosomePair) microsatXsomesPair.crossover()).xsomeStrands[i]);
		i = (int) (C_ContextCreator.randomGeneratorForGameteAndSexSelection.nextDouble() * 2);
		gametes.add(gonosome.crossover().xsomeStrands[i]);
		if (!this.isHybrid()) { // Cross the two chromosomes of each autosome pairs. Then put randomly one of the two newly
								// crossed
								// chromosome into the gamete. This is done for each autosome pairs.
			for (Iterator<C_ChromosomePair> iterator = this.bivalents.iterator(); iterator.hasNext();) {
				C_ChromosomePair autosomePair = (C_ChromosomePair) iterator.next();
				i = (int) (C_ContextCreator.randomGeneratorForGameteAndSexSelection.nextDouble() * 2);
				gametes.add(((C_ChromosomePair) autosomePair.crossover()).xsomeStrands[i]);
			}
		}
		else {// if the genome is hybrid, autosomes are segregated.
			i = (int) (C_ContextCreator.randomGeneratorForGameteAndSexSelection.nextDouble() * 2);
			gametes.addAll(this.segregation().get(i));
		}
		return gametes; // List of chromosomes (One for each pair).
	}
	/** The first chromosome of a multivalent are put randomly into one of two gametes. The other chromosomes of the multivalent
	 * have a biggest probability of being put in the other gamete. Author A. Comte and J.Le Fur 03/2012
	 * @return the list of possible gametes (containing multivalents) made by this this genome (even non-viable gametes). */
	protected ArrayList<ArrayList<C_Chromosome>> segregation() {
		ArrayList<ArrayList<C_Chromosome>> xsomeMultivalList = this.xsomeFission();
		// Initialization of a list of gametes.
		ArrayList<ArrayList<C_Chromosome>> gametesLeftAndRight = new ArrayList<ArrayList<C_Chromosome>>();
		ArrayList<C_Chromosome> gameteLeft = new ArrayList<C_Chromosome>();
		ArrayList<C_Chromosome> gameteRight = new ArrayList<C_Chromosome>();
		// for each each multivalent
		for (ArrayList<C_Chromosome> multivalList : xsomeMultivalList) {
			Collections.sort(multivalList); // in order to classify first the biggest xsomes.
			// for each each chromosome of the multivalent.
			for (C_Chromosome xsome : multivalList) {
				// If the gene has a version in gameteLeft, we put the chromosome in gameteRight
				for (C_Gene gene : xsome.getGenes()) {
					if (searchAGene(gene, gameteLeft)) {
						gameteRight.add(xsome);
						break; // break to add every chromosome only once.
					}
					// If the gene has a version in gameteRight, we put the chromosome in gameteLeft
					else if (searchAGene(gene, gameteRight)) {
						gameteLeft.add(xsome);
						break;// break to add every chromosome only once.
					}
					else {
						// If the gene has nor a version in gameteLeft nor in gameteRight, we put
						// the chromosome randomly in gameteRight or in gameteLeft.
						int i = (int) (C_ContextCreator.randomGeneratorForGameteAndSexSelection.nextDouble() * 2);
						if (i == 0) gameteLeft.add(xsome);
						else gameteRight.add(xsome);
						break;// break to add every chromosome only once.
					}
				}
			}
		}
		gametesLeftAndRight.add(gameteLeft);
		gametesLeftAndRight.add(gameteRight);
		return gametesLeftAndRight; // contains two complementary gametes (of autosome): gameteRight
									// and gameteLeft
	}
	/** Chromosomes pairs of hybrid autosomes are separated and strands that are constituted of several chromosomes (e.g., M.
	 * erythroleucus) are broken into these several original chromosomes. Author J.Le Fur and A. Comte 03/2012
	 * @return the list of autosomal multivalents. That is to say, a list of lists of chromosomes. */
	protected ArrayList<ArrayList<C_Chromosome>> xsomeFission() {
		// System.out.println(this + "C_GenomeEucaryote.xsomeFission(1)" + autosomes);
		// Initialization of a multivalent list (multivalent = a list of chromosomes with genes in
		// common and which gather during meiosis.
		boolean found = false;
		ArrayList<ArrayList<C_Chromosome>> xsomeMultivalList = new ArrayList<ArrayList<C_Chromosome>>();

		for (C_ChromosomePair oneAutosomePair : this.bivalents) { // For each chromosome pairs
			oneAutosomePair = (C_ChromosomePair) oneAutosomePair.crossover();
			// bearingXsomeList bears the split xsomes for one autosome pair
			ArrayList<C_Chromosome> bearingXsomeList = new ArrayList<C_Chromosome>();
			for (C_Chromosome oneStrandXsome : oneAutosomePair.xsomeStrands) {
				// If one strand is a merged xsome, position each of its genes in separate chromosomes.
				if (oneStrandXsome.isMergedXsome()) {
					for (int idxOneGeneMerged = 0; idxOneGeneMerged < oneStrandXsome
							.getNumGenes(); idxOneGeneMerged++) {
						C_Gene oneGeneToMerge = oneStrandXsome.getGene(idxOneGeneMerged);
						found = false;
						// Search if genes with the same initial ownerXsome were already positioned

						searchSignature :

						for (int idxBearingXsome = 0; idxBearingXsome < bearingXsomeList.size(); idxBearingXsome++) {
							C_Chromosome oneBearingXsome = bearingXsomeList.get(idxBearingXsome);
							for (C_Gene onePositionedGene : oneBearingXsome.getGenes()) {
								// if found, join the gene to the same xsome.
								if ((onePositionedGene.ownerXsomeNumber == oneGeneToMerge.ownerXsomeNumber)
										&& (onePositionedGene != oneGeneToMerge)) {
									oneBearingXsome.addGene(oneGeneToMerge);
									found = true;
									break searchSignature;
								}
							}
						}
						if (!found) {// initialization of a new chromosome for each gene and copy the gene on the new chromosome.
							C_Chromosome chromosome = new C_Chromosome(oneStrandXsome.mapLength, 0,
									oneStrandXsome.recombinator);
							chromosome.addGene(oneGeneToMerge);
							if (chromosome.getNumGenes() != 0) bearingXsomeList.add(chromosome);
						}
					}
				}
				// If the chromosome is an original(i.e., has not been merged).
				else {
					if (!bearingXsomeList.contains(oneStrandXsome)) bearingXsomeList.add(oneStrandXsome);
				}
			}
			xsomeMultivalList.add(bearingXsomeList);
		}
		// System.out.println(this + "C_GenomeEucaryote.xsomeFission(2)" + xsomeMultivalList);
		return xsomeMultivalList;
	}
	/** To know if a particular gene is present on a chromosome in a list of chromosomes (ie: a gamete) / Author J.Le Fur and A.
	 * Comte
	 * @return true (if the gene is in the list) or false (if the gene isn't) */
	private boolean searchAGene(C_Gene gene, ArrayList<C_Chromosome> gamete) {
		double mapLocation = gene.mapLoc;
		for (C_Chromosome xsome : gamete) {
			for (C_Gene gene2 : xsome.getGenes()) {
				if (gene2.mapLoc == mapLocation) return true;
			}
		}
		return false;
	}

	/** Fusion of the gametes of the mother and the father / Author J.Le Fur, A. Comte - 21.02.2012 mate has been split into mate
	 * and makeZygoteXsomes since, initially, only (e.g.) a mastomys genome can make a mastomys genome.
	 * @param mutRate
	 * @param partnerGenome
	 * @return the list of ChromosomePair of the genome of the future offspring. */
	protected ArrayList<C_ChromosomePair> makeZygoteXsomes(Object mutRate, I_DiploidGenome partnerGenome) {
		// zygoteXsomes is going to be the list of the chromosomePairs of the future genome.
		ArrayList<C_ChromosomePair> zygoteXsomes = new ArrayList<C_ChromosomePair>();
		if (this.gonosome.isFemale() && ((C_GenomeEucaryote) partnerGenome).gonosome.isFemale())
			System.err.println("C_GenomeEucaryote.makeZygoteXsomes(): bad female couple");
		else if (this.gonosome.isMale() && ((C_GenomeEucaryote) partnerGenome).gonosome.isMale())
			System.err.println("C_GenomeEucaryote.makeZygoteXsomes(): bad male couple");
		else {
			ArrayList<C_Chromosome> gametesThis = this.makeGametes();
			ArrayList<C_Chromosome> gametesPartner = ((C_GenomeEucaryote) partnerGenome).makeGametes();
			// xsomeMatch performs the compatibility check of the unbalanced caryotypes.
			if (gametesThis.size() != gametesPartner.size()) {
				ArrayList<ArrayList<C_Chromosome>> temp = xsomesMatch(gametesThis, gametesPartner);
				gametesThis = temp.get(0);
				gametesPartner = temp.get(1);
			}
			zygoteXsomes.add(new C_XsomePairMicrosat(gametesThis.get(MICROSAT_INDEX), gametesPartner.get(
					MICROSAT_INDEX)));
			zygoteXsomes.add(new C_XsomePairSexual(gametesThis.get(GONOSOME_INDEX), gametesPartner.get(
					GONOSOME_INDEX)));
			// in any autosomes case

			// THE HEART OF GENOMES CROSSING
			for (int i = BIVALENTS_INDEX; i < gametesThis.size(); i++) {
				C_Chromosome gameteThis = gametesThis.get(i);
				double gene1ThisMaploc = gametesThis.get(i).getGene(0).getMapLoc();
				// find the first common gene (genes have unique maplocs in the genome)
				for (int j = BIVALENTS_INDEX; j < gametesPartner.size(); j++) {
					C_Chromosome gametePartner = gametesPartner.get(j);
					double gene1PartnerMaploc = gametesPartner.get(j).getGene(0).getMapLoc();
					if (gene1PartnerMaploc == gene1ThisMaploc) {
						zygoteXsomes.add(new C_ChromosomePair(gameteThis, gametePartner));
						break;
					}
				}
			}
			// As it happens that some chromosomes are empty after the xsomeMatch procedure, this
			// final check removes the corresponding zygote.
			for (int i = BIVALENTS_INDEX; i < zygoteXsomes.size(); i++) {
				if (zygoteXsomes.get(i).getXsomeStrand(C_ChromosomePair.PARENT_1).getNumGenes() != zygoteXsomes.get(i)
						.getXsomeStrand(C_ChromosomePair.PARENT_2).getNumGenes()) {
					zygoteXsomes.get(MICROSAT_INDEX).getGene(LOCUS_LETHAL_ALLELE, STRAND_LETHAL_ALLELE).setAllele(
							LETHAL_ALLELE);
					hybridInspector.incrPbUnbalancedGene();
				}
			}
		}
		return zygoteXsomes;
	}
	/** If we are in the case of an hybrid with gametes of different sizes, we transform the gamete with the largest number of
	 * chromosomes into a list that matches the number of chromosomes of the smallest gametes (with the corresponding genes)in
	 * order to make pairs compatible with the chromosome pairs factory (cricketsim) CAUTION : every chromosomes are split into
	 * single genes. Author J.Le Fur and A. Comte 03/2012
	 * @param gametesThis , gametesPartner two gametes of different sizes
	 * @return the two gametes with the same size */
	protected ArrayList<ArrayList<C_Chromosome>> xsomesMatch(ArrayList<C_Chromosome> gametesThis,
			ArrayList<C_Chromosome> gametesPartner) {
		boolean geneAlreadyIncluded = false, geneFoundInLargest = false;
		boolean syntenieGeneFound = false;
		// identify the gamete with the largest number of xsomes.
		ArrayList<C_Chromosome> smallestGamete, largestGamete;
		ArrayList<C_Chromosome> returnSmallest = new ArrayList<C_Chromosome>();
		ArrayList<C_Chromosome> returnLargest = new ArrayList<C_Chromosome>();
		ArrayList<C_Gene> genesNotToTreatAnymore = new ArrayList<C_Gene>();
		ArrayList<ArrayList<C_Chromosome>> returnList = new ArrayList<ArrayList<C_Chromosome>>();
		if (gametesThis.size() < gametesPartner.size()) {
			smallestGamete = new ArrayList<C_Chromosome>(gametesThis);
			largestGamete = new ArrayList<C_Chromosome>(gametesPartner);
		}
		else {
			smallestGamete = new ArrayList<C_Chromosome>(gametesPartner);
			largestGamete = new ArrayList<C_Chromosome>(gametesThis);
		}
		C_Chromosome smallestGonosome = smallestGamete.get(GONOSOME_INDEX);
		C_Chromosome smallestMicrosat = smallestGamete.get(MICROSAT_INDEX);
		C_Chromosome largestGonosome = largestGamete.get(GONOSOME_INDEX);
		C_Chromosome largestMicrosat = largestGamete.get(MICROSAT_INDEX);
		smallestGamete.remove(smallestGonosome);
		smallestGamete.remove(smallestMicrosat);
		largestGamete.remove(largestGonosome);
		largestGamete.remove(largestMicrosat);
		// transformation of the genome of largestGamete to match the genome of smallestGamete in pairs.
		Collections.sort(smallestGamete);// sort in descending order
		for (int i = 0; i < smallestGamete.size(); i++) {
			C_Chromosome xsomeSource = smallestGamete.get(i);
			// we remove genes already added to others chromosomes ('syntenic' genes from largest)
			for (C_Gene geneSource : genesNotToTreatAnymore) {
				if (xsomeSource.genes.contains(geneSource)) xsomeSource.genes.remove(geneSource);
			}
			// xsomeTarget will be the result of the smallest xsomes fusion
			C_Chromosome xsomeTarget = new C_Chromosome(0, xsomeSource.recombinator);
			// Research of each genes of each autosome of the smallestGamete in the largestGamete.
			// temporary list to add at the end
			ArrayList<C_Gene> genesToAddToXsomeSource = new ArrayList<C_Gene>();
			for (int j = 0; j < xsomeSource.genes.size(); j++) {
				C_Gene oneGeneSource = xsomeSource.getGene(j);
				// we process only genes which were not already processed (included).
				if (!genesNotToTreatAnymore.contains(oneGeneSource)) {
					// scan if the gene were already included within an unmerged xsome
					geneAlreadyIncluded = false;
					for (C_Chromosome x : returnSmallest) {
						for (C_Gene y : x.genes) {
							if (y == oneGeneSource) geneAlreadyIncluded = true;
						}
					}
					if (!geneAlreadyIncluded) {
						geneFoundInLargest = false;
						double oneGeneSourceMapLoc = oneGeneSource.getMapLoc();
						Collections.sort(largestGamete);// sort in descending order
						for (int k = 0; k < largestGamete.size(); k++) {
							C_Chromosome oneXsomeFromLargestGamete = largestGamete.get(k);
							// if oneGeneSource is found in source gamete, proceed to adding this
							// gene from largestGamete
							if (oneXsomeFromLargestGamete.getLocusAllele(oneGeneSourceMapLoc) != "empty") {
								geneFoundInLargest = true;
								if (!oneXsomeFromLargestGamete.isMergedXsome()) {
									// check if the genes of oneXsomeLargest were not already added.
									// If the first gene (index 0) is contained then all other genes
									// are
									if (!xsomeTarget.genes.contains(oneXsomeFromLargestGamete.getGene(0))) {
										xsomeTarget.addAllGenes(oneXsomeFromLargestGamete.genes);
										// if the new chromosome is bigger than the original, then,
										// it is because it is composed of several merged
										// chromosomes coming from the largest gamete.
										if (xsomeTarget.genes.size() != oneXsomeFromLargestGamete.genes.size())
											xsomeTarget.setMergedXsome(true);
									}
									// Then, add in xsomeSource all genes of the original
									// xsomeTarget that has just been stuck.
									for (C_Gene gene : oneXsomeFromLargestGamete.genes) {
										syntenieGeneFound = false;
										if (xsomeSource.getGeneWithMaploc(gene.mapLoc) == "empty") {
											for (C_Chromosome xsomeSmallest : smallestGamete) {
												if (xsomeSmallest.getGeneWithMaploc(gene.mapLoc) != "empty") {
													syntenieGeneFound = true;
													if (xsomeSmallest.getNumGenes() != 0) {
														if (xsomeSmallest.isMergedXsome()) {
															genesToAddToXsomeSource.add((C_Gene) xsomeSmallest
																	.getGeneWithMaploc(gene.mapLoc));
															genesNotToTreatAnymore.add((C_Gene) xsomeSmallest
																	.getGeneWithMaploc(gene.mapLoc));
														}
														else {
															genesToAddToXsomeSource.addAll(xsomeSmallest.genes);
															genesNotToTreatAnymore.addAll(xsomeSmallest.genes);
														}
													}
													// to preserve the loop the genes will be added at the end of the process
													xsomeSource.setMergedXsome(true);
												}
											}
										}
										else syntenieGeneFound = true;
										if (!syntenieGeneFound) {
											smallestMicrosat.getGene(0).setAllele(LETHAL_ALLELE);
											System.err.println(
													"C_GenomeEucaryote.xsomesMatch(): autostop gene not found in smallest gamete");
											hybridInspector.incrPbSynteny();
											break;
										}
									}
								}
								else {
									xsomeTarget.addGene((C_Gene) oneXsomeFromLargestGamete.getGeneWithMaploc(
											oneGeneSourceMapLoc));
									if (xsomeTarget.genes.size() != oneXsomeFromLargestGamete.genes.size())
										xsomeTarget.setMergedXsome(true);
								}
								break;
							} // The loop did not break (gene not found in Largest)
						}
						if (!geneFoundInLargest) {
							smallestMicrosat.getGene(0).setAllele(LETHAL_ALLELE);
							System.err.println(this + " gene " + oneGeneSource + " not found in gamete");
							hybridInspector.incrPbGeneNotFound();
							break;
						}
					} // end of search for one gene in all chromosomes of the largest gamete.
				}
			} // end of scan for the genes of one chromosome of smallest gamete
			xsomeSource.addAllGenes(genesToAddToXsomeSource);
			// if xsomes are homologous it's ok, else the zygote is not viable.
			if (xsomeSource.getNumGenes() != 0) returnSmallest.add(xsomeSource);
			if (xsomeTarget.getNumGenes() != 0) returnLargest.add(xsomeTarget);
			if (returnSmallest.size() != returnLargest.size()) {
				System.err.println("Not the same size!");
			}
			// Chromosomes from the same pair should have the same number of genes in order to match
			// during mating. Else, the zygote die.
			else {
				for (int xsomes = 0; xsomes < returnSmallest.size(); xsomes++) {
					C_Chromosome xsomesSources = returnSmallest.get(xsomes);
					C_Chromosome xsomesTargets = returnLargest.get(xsomes);
					if ((xsomesSources.getNumGenes() != xsomesTargets.getNumGenes()) || ((xsomesSources
							.getNumGenes() == 0) || (xsomesTargets.getNumGenes() == 0))) {
						smallestMicrosat.getGene(0).setAllele(LETHAL_ALLELE);
						System.err.println(this + ": chromosomes finally do not match gene sizes");
						hybridInspector.incrPbMatching();
					}
				}
			}
		} // end of scan for the chromosomes of smallest gamete
		Collections.sort(returnLargest);
		Collections.sort(returnSmallest);
		returnSmallest.add(MICROSAT_INDEX, smallestMicrosat);
		returnSmallest.add(GONOSOME_INDEX, smallestGonosome);
		returnLargest.add(MICROSAT_INDEX, largestMicrosat);
		returnLargest.add(GONOSOME_INDEX, largestGonosome);
		returnList.add(returnSmallest);
		returnList.add(returnLargest);
		return (returnList);
	}
	/** Chooses the appropriate genome class for the zygote depending on the genome classes of its parents. upper class if hybrid,
	 * same class if pure. Transforms the list of chromosomes Pair of the offspring into a new genome which can be any type of
	 * genome, depending on the genomes of the parents. Author J.Le Fur, A. Comte 01/03/2012
	 * @param mutRate
	 * @param partnerGenome
	 * @return the zygote's diploid Genome */
	@Override
	public I_DiploidGenome mateGenomes(Object mutRate, I_DiploidGenome partnerGenome) {
		I_DiploidGenome zygoteGenome = null;
		ArrayList<C_ChromosomePair> zygoteXsomes = makeZygoteXsomes(mutRate, partnerGenome);
		C_XsomePairMicrosat microsat = (C_XsomePairMicrosat) zygoteXsomes.get(MICROSAT_INDEX);
		C_XsomePairSexual gonosome = (thing.dna.C_XsomePairSexual) zygoteXsomes.get(GONOSOME_INDEX);
		zygoteXsomes.remove(microsat);
		zygoteXsomes.remove(gonosome);
		try {
			Constructor<?> constructor = null;
			// if the cross is nat x ery, the new genome is neither nat nor ery, it's a Mastomys genome. But, if the cross is
			// Mastomys x nat/ery or
			// Mastomys x Mastomys the hybrid from this cross will be a GenomeMastomys too.

			if (isHybrid(zygoteXsomes) && (this.getClass() != partnerGenome.getClass().getSuperclass()) && (this
					.getClass().getSuperclass() != partnerGenome.getClass()) && (this.getClass() != partnerGenome
							.getClass())) {
				constructor = this.getClass().getSuperclass().getDeclaredConstructor(C_XsomePairMicrosat.class,
						C_XsomePairSexual.class, ArrayList.class);
			}
			else constructor = this.getClass().getDeclaredConstructor(C_XsomePairMicrosat.class,
					C_XsomePairSexual.class, ArrayList.class);
			constructor.setAccessible(true);
			zygoteGenome = (I_DiploidGenome) constructor.newInstance(microsat, gonosome, zygoteXsomes);
			// production code should handle these exceptions more gracefully
		} catch (InstantiationException x) {
			x.printStackTrace();
		} catch (IllegalAccessException x) {
			x.printStackTrace();
		} catch (InvocationTargetException x) {
			x.printStackTrace();
		} catch (NoSuchMethodException x) {
			x.printStackTrace();
		}
		// the function of survival of the zygote is a sinusoid. It's value is the highest (=1) at an hybridRatio of 1, 0 and 0,5
		// according to the theory of the hybrid breakdown. PbEpistasie is due to positive interactions between genes of the same
		// species that do not occur in some hybrids.
		// COMMENT HERE TO STOP THE HYBRID BREAKDOWN BARRIER
		double survival = 0.5 * (java.lang.Math.sin(4 * java.lang.Math.PI * ((C_GenomeEucaryote) zygoteGenome)
				.hybridRatio() + 0.5 * java.lang.Math.PI) + 1);

		if (!microsat.getAlleles().contains(C_GenomeEucaryote.LETHAL_ALLELE)) {
			if (C_ContextCreator.randomGeneratorForEpistasis.nextDouble() > survival) {
				microsat.getGene(LOCUS_LETHAL_ALLELE, STRAND_LETHAL_ALLELE).setAllele(LETHAL_ALLELE);
				hybridInspector.incrPbEpistasie();
			}
		}
		// The Haldane theory says that the heterogametic sex (here the males) of hybrids, is
		// sterile. Then, every male which is also an hybrid has no viable child.
		// COMMENT HERE TO STOP THE HALDANE BARRIER
		if ((((C_GenomeEucaryote) partnerGenome).gonosome.isMale() && ((C_GenomeEucaryote) partnerGenome).isHybrid())
				|| ((this.gonosome.isMale()) && (this.isHybrid()))) {
			microsat.getGene(LOCUS_LETHAL_ALLELE, STRAND_LETHAL_ALLELE).setAllele(LETHAL_ALLELE);
			hybridInspector.incrPbHaldane();
		}
		return zygoteGenome;
	}
	/** Count the number of genes coming from different chromosomes for each chromosome. If the count is done on the whole genome
	 * and not chromosome by chromosome, the count is going to be underestimated. Indeed, the offsprings of an inbreeding may have
	 * two chromosomes identical.
	 * @return The diploid number (2n): number of chromosome pairs, including gonosomes and microsatellites. */
	public int getDiploidNumber() {
		Integer realDiploidNumber = super.getDiploidNumber() + (2 * 2); // 2 for the microsat and the gonosomes (x2 because they
																		// are
																		// pairs)
		for (C_ChromosomePair xsomesPair : bivalents) {
			for (C_Chromosome xsome : xsomesPair.xsomeStrands) {
				Set<Integer> totalAutosomes = new HashSet<Integer>();
				for (int i = 0; i < xsome.getNumGenes(); i++) {
					totalAutosomes.add(xsome.getGene(i).ownerXsomeNumber);
				}
				realDiploidNumber += totalAutosomes.size();
			}
		}
		return realDiploidNumber;
	}
	/** Compute if the xsomes in the arguments are hybrid or not. original taxon is expressed as two decimals after the gene
	 * value. These decimal values are compared. Author J.Le Fur and A. Comte. March 2012 */
	@Deprecated
	protected boolean isHybrid(ArrayList<C_ChromosomePair> xsomes) {
		for (C_ChromosomePair xsomePair : xsomes) {
			int numGenePairs = xsomePair.numGenes();
			if (numGenePairs == 0)
				A_Protocol.event("C_GenomeEucaryote.isHybrid()", "no gene in genomeEucaryote IsHybrid()", isError);
			for (int locus = 0; locus < numGenePairs; locus++) {
				C_Gene gL = (C_Gene) xsomePair.xsomeStrands[C_ChromosomePair.PARENT_1].getGene(locus);
				C_Gene gR = (C_Gene) xsomePair.xsomeStrands[C_ChromosomePair.PARENT_2].getGene(locus);
				if (compareDecimals((Double) gL.getAllele(), (Double) gR.getAllele())) {}
				else return (true);
			}
		}
		return (false);
	}

	/** getter of the hybrid field, used for protocols beyond Masto ery. and Masto nat. */
	public boolean isHybrid() {
		return false;
	}
	/** getter of the hybrid field */
	public boolean isHybrid0() {
		return isHybrid;
	}
	/** returns the pair of chromosome among the genome, containing a gene at the given maploc */
	public C_ChromosomePair getChromosomePairFromMaploc(double maploc) {
		C_ChromosomePair xsomePair = null;
		for (C_ChromosomePair chromosomePair : this.bivalents) {
			List<C_Gene> genesLeft = chromosomePair.getXsomeStrand(C_ChromosomePair.PARENT_1).getGenes();
			// assume that geneRight and geneLeft maploc are identical and unique for the genome.
			for (C_Gene gene : genesLeft) {
				if (gene.mapLoc == maploc) xsomePair = chromosomePair;
			}
		}
		return xsomePair;
	}
	/** @return the ratio (number of genes natalensis)/(number of genes) Author A.Comte 11/05/2012 */
	public double hybridRatio() {// TODO JLF 2017.08 should be placed within hybridInspector
		double total = 0.;
		double totalNat = 0.;
		for (C_ChromosomePair xsomePair : this.getAutosomes()) {
			for (C_Chromosome xsome : xsomePair.xsomeStrands) {
				for (C_Gene gene : xsome.getGenes()) {
					total++;
					double alleleTaxon = (Double) gene.getTaxonSignature();
					if (alleleTaxon == 11.) totalNat++; // TODO number in source DNA; taxon signature of natalensis is 0.11
				}
			}
		}
		return totalNat / total;
	}
	public ArrayList<C_ChromosomePair> getAutosomes() {
		return bivalents;
	}
	/** taxonSignature is arbitrary, it is specific of the corresponding group. it corresponds to the decimal part of the alleles
	 * value Author J. Le Fur & A. Comte 03/2012, rev. JLF 08.2014 */
	protected Double getTaxonSignature() {
		return 0.00;
	}
	public static void init(C_InspectorHybrid inspector) {
		hybridInspector = inspector;// we declare the hybrid inspector that catches the lethal allele in various situations JLF
									// 02.2013
	}
	/** utility to perform the comparison of decimals values Author J.Le Fur august 2012 */
	public static boolean compareDecimals(double n1, double n2) {
		return truncate(n1) == truncate(n2);
	}
	/** utility to extract the decimals values Author J.Le Fur august 2012 x is supposed to have a maximum of two decimal
	 * digits */
	private static double truncate(double x) {
		long y = (long) (x * 1000.);
		y = y - ((int) x) * 1000;
		return y;
	}
	/** Returns the pair of chromosomes microsatellite of the Genome Eucaryote. */
	public C_XsomePairMicrosat getMicrosatXsome() {
		return microsatXsomesPair;
	}
	/** Returns the pair of gonosome of the Genome Eucaryote. */
	public C_XsomePairSexual getGonosome() {
		return gonosome;
	}
	public void setGonosome(C_XsomePairSexual gonosome) {
		this.gonosome = gonosome;
	}
	public Map<String, Double> getAlleles() {
		return alleles;
	}
}