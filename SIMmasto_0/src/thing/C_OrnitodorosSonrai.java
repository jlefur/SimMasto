package thing;

import java.util.TreeSet;

import data.C_Parameters;
import data.constants.I_ConstantDodel2;
import data.converters.C_ConvertTimeAndSpace;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import simmasto0.util.C_VariousUtilities;
import thing.dna.C_GenomeEucaryote;
import thing.dna.I_DiploidGenome;
import thing.dna.species.C_GenomeAcaria;
import thing.ground.C_BurrowSystem;
import thing.ground.C_SoilCellUrban;
import thing.ground.I_Container;

/** This class accounts for Tick .
 * @author M. Sall 10.2019 */
public class C_OrnitodorosSonrai extends A_Animal implements I_ConstantDodel2, I_ReproducingThing {
    //
    // FIELDS
    //
    private double curMealDuration_Umn; // hanging duration for the meal
    private double hibernationDuration_Uday; // starvation duration in a burrow
    // private boolean canSpawn;// TODO MS de JLF 2020.04 utiliser la procédure (override de Amniote) TODO MS 2020.04 Ornithodoros
    // is not an Amniote
    private boolean hasEaten; // Allow to know if agent has eaten
    protected double curMatingLatency_Uday;
    protected double curGestationLength_Uday;
    private String stasis; // different stasis (egg, larvae, nymph and adult) of tick in its evolution
    protected TreeSet<C_Egg> eggList;// Simple data structure to hold zygote information during matingLatency
    private int biteNumber;
    //
    // CONSTRUCTOR
    //
    public C_OrnitodorosSonrai(I_DiploidGenome genome) {
        super(genome);
        this.hibernationDuration_Uday = 0.;
        this.curMealDuration_Umn = 0.;
        this.hasEaten = false;
        this.curGestationLength_Uday = 0.;
        this.curMatingLatency_Uday = MATING_LATENCY_DURATION_Uday;
        this.eggList = new TreeSet<C_Egg>();
        this.setBiteNumber(0);
    }
    //
    // OVERRIDEN METHODS
    //
    @Override
    /** If its current position is a burrow, all burrow containers can be perceived by the tick; If tick is in feeding stage it stay trapped, no
     * cognitive process! */
    protected TreeSet<I_SituatedThing> perception() {
        if (this.getCurrentSoilCell() instanceof C_BurrowSystem) {
            TreeSet<I_SituatedThing> burrowOccupants = (TreeSet<I_SituatedThing>) this.getCurrentSoilCell().getOccupantList().clone();
            burrowOccupants.remove(this);
            return burrowOccupants;
        }
        TreeSet<I_SituatedThing> perceivedThings = super.perception();
        // TODO JLF&MS 2020.09 generalize obstacle perception ?
        TreeSet<I_SituatedThing> reallyPerceivedThings = new TreeSet<I_SituatedThing>();
        for (I_SituatedThing oneThing : perceivedThings) {
            I_SituatedThing sightObstacle = C_VariousUtilities.checkObstacleBefore0(this, oneThing);
            if (!(sightObstacle != null && sightObstacle != oneThing)) reallyPerceivedThings.add(oneThing);
        }
        return reallyPerceivedThings;
    }
    @Override
    /** Return burrows perceived if it's no trapped and its current position is the ground */
    protected TreeSet<I_SituatedThing> deliberation(TreeSet<I_SituatedThing> perceivedThings) {
        TreeSet<I_SituatedThing> selectedThings = new TreeSet<I_SituatedThing>();
        if (!perceivedThings.isEmpty()) {
            if (this.getDesire().equals(HIDE)) selectedThings = this.chooseShelter(perceivedThings);
            else if (this.getDesire().equals(REPRODUCE)) {
                selectedThings = this.choosePartner(perceivedThings);
                if (selectedThings.isEmpty() && this.hibernationDuration_Uday >= STARVING_DURATION_Uday) this.setDesire(FEED);
            }
            else if (this.getDesire().equals(FEED)) selectedThings = super.deliberation(perceivedThings);
        }
        return selectedThings;
    }
    @Override
    public void step_Utick() {
        this.updatePhysiologicStatus();
        super.step_Utick();
    }
    @Override
    /** return the potential perceived preys */
    public TreeSet<I_SituatedThing> chooseFood(TreeSet<I_SituatedThing> perceivedThings) {
        TreeSet<I_SituatedThing> perceivedRodents = new TreeSet<I_SituatedThing>();
        for (I_SituatedThing oneThing : perceivedThings)
            if (oneThing instanceof C_Rodent) perceivedRodents.add(oneThing);
        return perceivedRodents;
    }
    @Override
    /** return the potential perceived shelter */
    public TreeSet<I_SituatedThing> chooseShelter(TreeSet<I_SituatedThing> perceivedThings) {
        TreeSet<I_SituatedThing> selectedShelters = new TreeSet<I_SituatedThing>();
        for (I_SituatedThing oneShelter : perceivedThings)
            if ((oneShelter instanceof C_BurrowSystem && (oneShelter.getCurrentSoilCell().getAffinity() != STREET_AFFINITY)) || this.isSightObstacle(oneShelter))
                selectedShelters.add(oneShelter);
        if (selectedShelters.size() != 0) return selectedShelters;
        return super.chooseShelter(perceivedThings);
    }
    @Override
    /** Process to action corresponding to the selected desire */
    protected boolean processTarget() {
        if (this.target instanceof C_Rodent && this.getDesire().equals(FEED) && !this.getStasis().equals(EGG))
            // if (!A_Protocol.protocolCalendar.isDawn() && !A_Protocol.protocolCalendar.isDayTime())
            return actionEat();
        if (this.getDesire().equals(REPRODUCE) && this.target instanceof C_OrnitodorosSonrai) {
            if (this.isreadyToMate() && this.canMateWith((A_Animal) this.target)) {
                if (this.testFemale()) {
                    this.curMatingLatency_Uday = 0.;
                    return this.actionMateWithMale((I_ReproducingThing) this.target);
                }
                else {
                    ((C_OrnitodorosSonrai) this.target).curMatingLatency_Uday = 0.;
                    return ((C_OrnitodorosSonrai) this.target).actionMateWithMale(this);
                }
            }
            else return true;
        }
        else if (this.getDesire().equals(HIDE)) return this.actionHide();
        return super.processTarget();
    }
    @Override
    /** Female mating with a selected male */
    public boolean actionMateWithMale(I_ReproducingThing male) {
        I_DiploidGenome eggGenome;
        C_GenomeAcaria maleParentGenome = (C_GenomeAcaria) ((A_Animal) male).getGenome();
        C_GenomeAcaria FemaleParentGenome = (C_GenomeAcaria) this.genome;
        // generate LITTER_SIZE zygotes (fusion, cross-over (& mutation)) from the mother and father
        int litterSize = FemaleParentGenome.getLitterSizeValue();
        for (int i = 0; i < litterSize; i++) {
            eggGenome = this.genome.mateGenomes(0, maleParentGenome);
            // If one of the microsatelite of the genome of the egg contains the Lethal_Allele (for a reason or an other). The egg
            // is killed. Then, the size of the litter will be smaller.
            if (((C_GenomeEucaryote) eggGenome).getMicrosatXsome().getAlleles().contains(C_GenomeEucaryote.LETHAL_ALLELE))
                if (C_Parameters.VERBOSE) A_Protocol.event("A_Mammal.actionMateWithMale", "EGG NIPPED IN THE BUD :'(", isNotError);
                else this.eggList.add(new C_Egg(eggGenome));
        }
        if (!this.eggList.isEmpty()) {
            this.setDesire("");
            return true;
        }
        return false;
    }
    @Override
    protected void actionNoChoice() {
        if (this.inDangerousArea()) this.actionDisperse();
        // else super.actionNoChoice();
    }
    @Override
    /** Eating process of tick, only egg can't take blood meal ! larvae and nymph take a blood meal to grow up and moult; adult eat to produce a
     * viable egg and to stay alive */
    protected boolean actionEat() {
        this.setBiteNumber(biteNumber + 1);
        if (this.isInfected()) this.actionInfect(this.target);
        if (this.target.isInfected()) this.target.actionInfect(this);
        this.setTrappedOnBoard(true);
        C_ContextCreator.protocol.contextualizeOldThingInCell(this, (I_Container) this.target);
        resetCurMealDuration_Umn();
        this.setDesire(NONE);
        this.hasEaten = true;
        return super.actionEat();
    }
    @Override
    /** Do nothing when tick hasn't choice */
    public boolean actionHide() {
        if (!(this.target instanceof C_BurrowSystem)) this.actionDisperse();
        else {
            this.actionEnterContainer((I_Container) this.target);
            this.setDesire("");
        }
        return true;
    }
    @Override
    public A_Animal giveBirth(I_DiploidGenome genome) {
        return new C_OrnitodorosSonrai(genome);
    }
    @Override
    /** Check if thing is a wall */
    public boolean isSightObstacle(I_SituatedThing thing) {
        if (thing instanceof C_SoilCellUrban && ((C_SoilCellUrban) thing).isWall()) return true;
        else return super.isSightObstacle(thing);
    }
    //
    // OTHERS METHODS
    //
    /** Return all perceived preys */
    public boolean perceivedPrey(TreeSet<I_SituatedThing> perceivedThings) {
        for (I_SituatedThing oneTick : perceivedThings)
            if (oneTick instanceof C_RodentDomestic2) return true;
        return false;
    }
    /** Manage ticks physiology <br>
     */
    protected void updatePhysiologicStatus() {
        this.updateStasis();
        // this.setBiteNumber(0);// TODO MS 2020.09 H0 a tick cannot bite more than once during a time step
        if (this.isTrappedOnBoard()) {
            this.curMealDuration_Umn -= oneDay_Umn / C_ConvertTimeAndSpace.oneDay_Utick;
            if (this.curMealDuration_Umn <= 0.) {
                if ((this.getCurrentSoilCell() instanceof C_RodentDomestic2))
                    C_ContextCreator.protocol.contextualizeOldThingInCell(this, this.getCurrentSoilCell().getCurrentSoilCell());
                this.setTrappedOnBoard(false);
                if (!(this.getCurrentSoilCell() instanceof C_BurrowSystem)) this.setDesire(HIDE);
                this.hibernationDuration_Uday = 0.;
            }
        }
        else {
            if (this.canSpawn() && (this.currentSoilCell instanceof C_BurrowSystem)) this.actionSpawn();
            if (this.isreadyToMate()) this.setDesire(REPRODUCE);
            if (!this.getStasis().equals(EGG)) this.hibernationDuration_Uday += 1 / C_ConvertTimeAndSpace.oneDay_Utick;
            if (this.hibernationDuration_Uday >= STARVING_DURATION_Uday) {
                if (this.getDesire() != FEED) this.setDesire(FEED);
                if (this.hasEaten) this.hasEaten = false;
            }
        }
        if (this.isPregnant()) this.curGestationLength_Uday += 1 / C_ConvertTimeAndSpace.oneDay_Utick;
        else if (this.getStasis().equals(ADULT) && !this.isreadyToMate()) this.curMatingLatency_Uday += 1 / C_ConvertTimeAndSpace.oneDay_Utick;
        if (this.inDangerousArea()) this.setDesire(HIDE);
    }
    /** return the potential perceived partners */
    public TreeSet<I_SituatedThing> choosePartner(TreeSet<I_SituatedThing> perceivedThings) {
        TreeSet<I_SituatedThing> perceivedPartner = new TreeSet<I_SituatedThing>();
        for (I_SituatedThing oneThing : perceivedThings)
            if (oneThing instanceof C_OrnitodorosSonrai && ((C_OrnitodorosSonrai) oneThing).isreadyToMate()) perceivedPartner.add(oneThing);
        return perceivedPartner;
    }
    /** Manage tick evolution stasis Source: R.Tiwari_2017: General life cycle of ticks */
    public void updateStasis() {
        if (this.getStasis().equals(EGG)) {
            if (this.getAge_Uday() >= EGG_STASIS_DURATION_Uday) {
                this.setStasis(LARVAE);
                this.setDesire(FEED);
            }
        }
        else if (this.getStasis().equals(LARVAE)) {
            if (this.getAge_Uday() >= LARVAE_STASIS_DURATION_Uday) this.setStasis(NYMPH);
        }
        else if (this.getStasis().equals(NYMPH)) {
            if (this.getAge_Uday() >= NYMPH_STASIS_DURATION_Uday && this.hasEaten) this.setStasis(ADULT);//
        }
    }
    /** Spawn two egg; one female egg and one male egg before blood meal */
    public void actionSpawn() {
        for (C_Egg egg : this.eggList) {
            A_Animal child = this.giveBirth(egg.genome);
            myLandscape.addChildAgent(this, child);
            child.setDesire(NONE);// used to bypass activity steps
            child.setMyName(child.retrieveMyName());
            child.setHasToSwitchFace(true);
            child.setAge_Uday(0.);
            ((C_OrnitodorosSonrai) child).setStasis(EGG);
            this.energy_Ukcal--;
        }
        this.eggList.clear();
        this.curGestationLength_Uday = 0.;
    }
    /** Restore the meal duration after eating
     * @see actionEat */
    public void resetCurMealDuration_Umn() {
        this.curMealDuration_Umn = MEAL_DURATION_Umn;
    }
    private boolean inDangerousArea() {
        int affinity = 0;
        if (this.getCurrentSoilCell() instanceof C_BurrowSystem) affinity = this.getCurrentSoilCell().getCurrentSoilCell().getAffinity();
        else affinity = this.getCurrentSoilCell().getAffinity();
        if (affinity == ROAD_AFFINITY || affinity == STREET_AFFINITY || affinity == TRACK_AFFINITY || affinity == MARKET_AFFINITY) return true;
        return false;

    }
    //
    // SETTERS && GETTERS
    //
    public void setStasis(String stasis) {
        this.stasis = stasis;
    }
    /** Give a random age to this and its corresponding stasis */
    public void setRandomAge() {
        long randAge_Uday = Math.round(((C_GenomeAcaria) this.genome).getMaxAge_Uday() / 2 * C_ContextCreator.randomGeneratorForInitialisation
                .nextDouble());
        this.setAge_Uday(randAge_Uday);
        if (this.getAge_Uday() < EGG_STASIS_DURATION_Uday) {
            this.setStasis(EGG);
            this.setDesire(FEED);
        }
        else if (this.getAge_Uday() < LARVAE_STASIS_DURATION_Uday) this.setStasis(LARVAE);
        else if (this.getAge_Uday() < NYMPH_STASIS_DURATION_Uday) this.setStasis(NYMPH);
        else this.setStasis(ADULT);
    }
    public void setBiteNumber(int biteNumber) {
        this.biteNumber = biteNumber;
    }
    /** Check if it is ready to mate */
    public boolean isreadyToMate() {
        return (!this.isPregnant() && (this.getStasis() == ADULT) && (this.curMatingLatency_Uday >= MATING_LATENCY_DURATION_Uday));
    }
    public boolean isPregnant() {
        return (this.eggList.size() != 0);
    }
    public String getStasis() {
        return this.stasis;
    }
    /** Get if it can Spawn */
    public boolean canSpawn() {
        if (isPregnant() && (this.curGestationLength_Uday > GESTATION_DURATION_Uday) && this.hasEaten) return true;
        return false;
    }
    /** Get if it can mate with the targeting tick */
    public boolean canMateWith(A_Animal oneTick) {
        if (oneTick.getGenome().getClass() == this.genome.getClass())
            if (!this.haveSameSex(oneTick) && ((C_OrnitodorosSonrai) oneTick).isreadyToMate()) return true;
        return false;
    }
    /** Verify if it has the same sex than the targeting tick */
    public boolean haveSameSex(A_Animal oneTick) {
        if ((this.testFemale() && oneTick.testFemale()) || (this.testMale() && oneTick.testMale())) return true;
        return false;
    }
    /** Return its current meal duration */
    public double getCurrentMealDuration() {
        return this.curMealDuration_Umn;
    }
    /** Return the spending time without taking blood meal */
    public double getHibernationDuration_Uday() {
        return this.hibernationDuration_Uday;
    }
    /** Return the number of bite in the current step */
    public int getBiteNumber() {
        return this.biteNumber;
    }
}
