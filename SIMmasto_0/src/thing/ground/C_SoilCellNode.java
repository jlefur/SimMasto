package thing.ground;

/** Node Class from A* algorithm
 * @author Marcelo Surriabre 2018, adapted J. Le Fur, april 2022 */
public abstract class C_SoilCellNode extends C_SoilCellGraphed {

	private int g;
	private int f;
	private int h;
	private boolean isBlock;
	private C_SoilCellNode parent;

	public C_SoilCellNode(int aff, int lineNo, int colNo) {
		super(aff, lineNo, colNo);
	}

	public void calculateHeuristic(C_SoilCellNode finalNode) {
		this.h = Math.abs(finalNode.retrieveLineNo() - this.lineNo) + Math.abs(finalNode.retrieveColNo() - this.colNo);
	}

	public void setNodeData(C_SoilCellNode currentNode, int cost) {
		int gCost = currentNode.g + cost;
		this.parent = currentNode;
		this.g = gCost;
		calculateFinalCost();
	}

	public boolean checkBetterPath(C_SoilCellNode currentNode, int cost) {
		int gCost = currentNode.g + cost;
		if (gCost < this.g) {
			setNodeData(currentNode, cost);
			return true;
		}
		return false;
	}

	private void calculateFinalCost() {
		int finalCost = this.g + this.h;
		this.f = finalCost;
	}
	@Override
	public boolean equals(Object arg0) {
		C_SoilCellNode other = (C_SoilCellNode) arg0;
		return this.retrieveLineNo() == other.retrieveLineNo() && this.retrieveColNo() == other.retrieveColNo();
	}
	@Override
	public String toString() {
		return "Node [row=" + this.lineNo + ", col=" + this.colNo + "]";
	}
	public int getF() {
		return f;
	}
	public C_SoilCellNode getParent() {
		return parent;
	}
	public boolean isBlock() {
		return this.isBlock;
	}
}
