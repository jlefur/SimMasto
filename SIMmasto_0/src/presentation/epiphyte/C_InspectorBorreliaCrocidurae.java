package presentation.epiphyte;

import java.util.TreeSet;

import thing.C_BorreliaCrocidurae;

/** Data inspector: retrieves informations about ticks.
 * @author M SALL 04.2020 */
public class C_InspectorBorreliaCrocidurae extends A_Inspector {
    //
    // FIELD
    //
    private TreeSet<C_BorreliaCrocidurae> borreliaList = new TreeSet<C_BorreliaCrocidurae>();
    //
    // CONSTRUCTOR
    //
    public C_InspectorBorreliaCrocidurae() {
        super();
        this.borreliaList.clear();
        // add to the super class header, this proper header
        this.indicatorsHeader = "Borrelia;Bite Number;Hibernation Average;Birth Number;Population";
    }
    //
    // METHODS
    //
    public void addBorreliaToList(C_BorreliaCrocidurae oneBorrelia) {
        if (!this.borreliaList.add(oneBorrelia)) System.out.println("C_InspectorOrnithodorosSonrai.addTickToList() : : could not add" + oneBorrelia);
    }
    //
    // SETTER & GETTERS
    //
    public TreeSet<C_BorreliaCrocidurae> getBorreliaList(){
    	return this.borreliaList;
    }
}
