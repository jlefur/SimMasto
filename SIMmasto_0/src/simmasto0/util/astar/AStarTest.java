package simmasto0.util.astar;

import java.util.List;

import thing.ground.C_SoilCellNode;
import thing.ground.C_SoilCellUrban;

public class AStarTest {

	public static void main(String[] args) {
		C_SoilCellUrban initialCellNode = new C_SoilCellUrban(0, 2, 1);
		C_SoilCellUrban finalCellNode = new C_SoilCellUrban(0, 2, 5);
		AStar aStar = new AStar();
		// define obstacles
		int[][] blocksArray = new int[][]{{1, 3}, {2, 3}, {3, 3}};
		List<C_SoilCellNode> path = aStar.findPath(initialCellNode,finalCellNode);
		for (C_SoilCellNode node : path) {
			System.out.println(node);
		}

		// Search Area
		// 0 1 2 3 4 5 6
		// 0 - - - - - - -
		// 1 - - - B - - -
		// 2 - I - B - F -
		// 3 - - - B - - -
		// 4 - - - - - - -
		// 5 - - - - - - -

		// Expected output with diagonals
		// C_SoilCellUrban [row=2, col=1]
		// C_SoilCellUrban [row=1, col=2]
		// C_SoilCellUrban [row=0, col=3]
		// C_SoilCellUrban [row=1, col=4]
		// C_SoilCellUrban [row=2, col=5]

		// Search Path with diagonals
		// 0 1 2 3 4 5 6
		// 0 - - - * - - -
		// 1 - - * B * - -
		// 2 - I* - B - *F -
		// 3 - - - B - - -
		// 4 - - - - - - -
		// 5 - - - - - - -

		// Expected output without diagonals
		// C_SoilCellUrban [row=2, col=1]
		// C_SoilCellUrban [row=2, col=2]
		// C_SoilCellUrban [row=1, col=2]
		// C_SoilCellUrban [row=0, col=2]
		// C_SoilCellUrban [row=0, col=3]
		// C_SoilCellUrban [row=0, col=4]
		// C_SoilCellUrban [row=1, col=4]
		// C_SoilCellUrban [row=2, col=4]
		// C_SoilCellUrban [row=2, col=5]

		// Search Path without diagonals
		// 0 1 2 3 4 5 6
		// 0 - - * * * - -
		// 1 - - * B * - -
		// 2 - I* * B * *F -
		// 3 - - - B - - -
		// 4 - - - - - - -
		// 5 - - - - - - -
	}
}
