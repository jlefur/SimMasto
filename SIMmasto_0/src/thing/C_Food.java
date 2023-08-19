package thing;

import thing.ground.A_SupportedContainer;

/**Extends A_Container since Food can be infected by organisms*/
public class C_Food extends A_SupportedContainer {
    public C_Food(int affinity, int lineNo, int colNo) {
        this.affinity = affinity;
        this.lineNo = lineNo;
        this.colNo = colNo;
    }
    @Override
    public int getCarryingCapacity_Urodent() {
        return 0;
    }

}
