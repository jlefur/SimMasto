package simmasto0.util.astar;

import java.util.*;

import thing.A_VisibleAgent;
import thing.ground.C_SoilCellNode;
import thing.ground.I_Container;

/** A Star Algorithm
 * @author Marcelo Surriabre 2017, adapted J. Le Fur, april 2022 */
public class AStar {
	private static int DEFAULT_HV_COST = 10; // Horizontal - Vertical Cost
	private static int DEFAULT_DIAGONAL_COST = 14;
	private int hvCost;
	private int diagonalCost;
	private I_Container[][] searchArea;
	private PriorityQueue<C_SoilCellNode> openList;
	private Set<C_SoilCellNode> closedSet;
	private C_SoilCellNode initialCellNode;
	private C_SoilCellNode finalCellNode;

	public AStar() {
		this.hvCost = DEFAULT_HV_COST;
		this.diagonalCost = DEFAULT_DIAGONAL_COST;
		this.searchArea = A_VisibleAgent.myLandscape.getGrid();
	}

	public ArrayList<C_SoilCellNode> findPath(C_SoilCellNode initialCellNode, C_SoilCellNode finalCellNode) {
		this.initialCellNode = initialCellNode;
		this.finalCellNode = finalCellNode;
		this.openList = new PriorityQueue<C_SoilCellNode>(new Comparator<C_SoilCellNode>() {
			@Override
			public int compare(C_SoilCellNode node0, C_SoilCellNode node1) {
				return Integer.compare(node0.getF(), node1.getF());
			}
		});
		this.setCellNodes();
		this.closedSet = new HashSet<>();
		this.openList.add(initialCellNode);
		while (!isEmpty(this.openList)) {
			C_SoilCellNode currentCellNode = this.openList.poll();
			this.closedSet.add(currentCellNode);
			if (isFinalCellNode(currentCellNode)) return getPath(currentCellNode);
			else addAdjacentCellNodes(currentCellNode);
		}
		return new ArrayList<C_SoilCellNode>();
	}

	private void setCellNodes() {
		for (int i = 0; i < searchArea.length; i++) {
			for (int j = 0; j < searchArea[0].length; j++) {
				((C_SoilCellNode) this.searchArea[i][j]).calculateHeuristic(this.finalCellNode);
			}
		}
	}

	private ArrayList<C_SoilCellNode> getPath(C_SoilCellNode currentCellNode) {
		ArrayList<C_SoilCellNode> path = new ArrayList<C_SoilCellNode>();
		path.add(currentCellNode);
		C_SoilCellNode parent;
		while ((parent = currentCellNode.getParent()) != null //
				&& parent.getParent() != currentCellNode) {// added to avoid loops JLF 05.2022
			path.add(0, parent);
			currentCellNode = parent;
		}
		return path;
	}

	private void addAdjacentCellNodes(C_SoilCellNode currentCellNode) {
		addAdjacentUpperRow(currentCellNode);
		addAdjacentMiddleRow(currentCellNode);
		addAdjacentLowerRow(currentCellNode);
	}

	private void addAdjacentLowerRow(C_SoilCellNode currentCellNode) {
		int row = currentCellNode.retrieveLineNo();
		int col = currentCellNode.retrieveColNo();
		int lowerRow = row + 1;
		if (lowerRow < this.searchArea.length) {
			if (col - 1 >= 0) {
				// Comment this line if diagonal movements are not allowed
				checkCellNode(currentCellNode, col - 1, lowerRow, this.diagonalCost);
			}
			if (col + 1 < this.searchArea[0].length) {
				// Comment this line if diagonal movements are not allowed
				checkCellNode(currentCellNode, col + 1, lowerRow, this.diagonalCost);
			}
			checkCellNode(currentCellNode, col, lowerRow, this.hvCost);
		}
	}

	private void addAdjacentMiddleRow(C_SoilCellNode currentCellNode) {
		int row = currentCellNode.retrieveLineNo();
		int col = currentCellNode.retrieveColNo();
		int middleRow = row;
		if (col - 1 >= 0) {
			checkCellNode(currentCellNode, col - 1, middleRow, this.hvCost);
		}
		if (col + 1 < this.searchArea[0].length) {
			checkCellNode(currentCellNode, col + 1, middleRow, this.hvCost);
		}
	}

	private void addAdjacentUpperRow(C_SoilCellNode currentCellNode) {
		int row = currentCellNode.retrieveLineNo();
		int col = currentCellNode.retrieveColNo();
		int upperRow = row - 1;
		if (upperRow >= 0) {
			if (col - 1 >= 0) {
				// Comment this if diagonal movements are not allowed
				checkCellNode(currentCellNode, col - 1, upperRow, this.diagonalCost);
			}
			if (col + 1 < this.searchArea[0].length) {
				// Comment this if diagonal movements are not allowed
				checkCellNode(currentCellNode, col + 1, upperRow, this.diagonalCost);
			}
			checkCellNode(currentCellNode, col, upperRow, this.hvCost);
		}
	}

	private void checkCellNode(C_SoilCellNode currentCellNode, int col, int row, int cost) {
		C_SoilCellNode adjacentCellNode = (C_SoilCellNode) this.searchArea[row][col];
		if (!adjacentCellNode.isBlock() && !closedSet.contains(adjacentCellNode)) {
			if (!getOpenList().contains(adjacentCellNode)) {
				adjacentCellNode.setNodeData(currentCellNode, cost);
				getOpenList().add(adjacentCellNode);
			}
			else {
				boolean changed = adjacentCellNode.checkBetterPath(currentCellNode, cost);
				if (changed) {
					// Remove and Add the changed node, so that the PriorityQueue can sort again its
					// contents with the modified "finalCost" value of the modified node
					getOpenList().remove(adjacentCellNode);
					getOpenList().add(adjacentCellNode);
				}
			}
		}
	}

	private boolean isFinalCellNode(C_SoilCellNode currentCellNode) {
		return currentCellNode.equals(finalCellNode);
	}

	private boolean isEmpty(PriorityQueue<C_SoilCellNode> openList) {
		return openList.size() == 0;
	}

	public PriorityQueue<C_SoilCellNode> getOpenList() {
		return openList;
	}

	public void setOpenList(PriorityQueue<C_SoilCellNode> openList) {
		this.openList = openList;
	}

	private void setDiagonalCost(int diagonalCost) {
		this.diagonalCost = diagonalCost;
	}
}
