/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground.landscape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.I_ConstantDodel2;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.NdPoint;
import simmasto0.protocol.A_Protocol;
import thing.A_Animal;
import thing.A_VisibleAgent;
import thing.ground.C_Concession;
import thing.ground.C_LandPlot;
import thing.ground.C_Room;
import thing.ground.C_SoilCellUrban;

/** The global container of a given protocol.<br>
 * 1/ Owns a grid/matrix with values ('affinity'), landplots of cells with the same affinity values.<br>
 * Values are read from a file. It can be an ASCII text grid or an image raster. (the image must be in grey levels or in 256 or
 * less colors). affinity values are stored in a gridValueLayer (as well as in the cell container attributes) <br>
 * 2/ Owns a continuous space
 * @see A_Protocol
 * @author Baduel 2009.04, Le Fur 2009.12, Longueville 2011.02, Le Fur 02.2011, 07.2012, 04.2015<br>
 *         rev. JLF 10.2015, 11.2015 - was formerly C_Raster <br>
 *         TODO JLF 2020.04 Should be normally in ground package */
public class C_LandscapeUrban extends C_Landscape implements I_ConstantDodel2 {
	//
	// CONSTRUCTOR
	//
	/** Constructor of grid ground : creates a gridValueLayer with values (affinity); an equivalent matrix with field agents, a
	 * continuous space where agents have real coordinates. Retrieves also for that the user parameters provided within the GUI */
	public C_LandscapeUrban(Context<Object> context, String url, String gridValueName, String continuousSpaceName) {
		super(context, url, gridValueName, continuousSpaceName);
	}
	//
	// METHODS
	//
	/** Initialize both (!) gridValueLayer and container matrix with C_SoilCellUrbanUrban
	 * @param matriceLue the values read in the raster, bitmap<br>
	 *            rev. JLF 02.2022 */
	@Override
	public void createGround(int[][] matriceLue) {
		for (int i = 0; i < this.dimension_Ucell.width; i++) {
			for (int j = 0; j < this.dimension_Ucell.height; j++) {
				int affinity = matriceLue[i][j];
				this.gridValueLayer.set(affinity, i, j);
				this.grid[i][j] = new C_SoilCellUrban(affinity, i, j);
			}
		}
	}
	/** Uses affinity values of the soil cells in this.grid to delineate landplots of equivalent affinities, put them in
	 * this.affinityLandPlots<br>
	 * If SoilCell of house, room, shop, or market is not in contact with two same SoilCells it will not be add in the current
	 * LandPlot */
	@Override
	public TreeSet<C_LandPlot> identifyAffinityLandPlots(Context<Object> context) {
		int i = 0, j = 0, x, y, x0, y0, k, affinity0;
		if (this.landPlots == null) this.landPlots = new TreeSet<C_LandPlot>();
		C_LandPlot newPlot;
		C_SoilCellUrban soilCell_0, soilCell_1;
		List<C_SoilCellUrban> fileDattente = new ArrayList<C_SoilCellUrban>();
		Set<C_SoilCellUrban> fileDattente2 = new HashSet<C_SoilCellUrban>();
		C_SoilCellUrban oneSoilCell = null;
		// Scan all the soilCellMatrix
		for (i = 0; i < this.dimension_Ucell.getWidth(); i++) {
			for (j = 0; j < this.dimension_Ucell.getHeight(); j++) {
				oneSoilCell = (C_SoilCellUrban) grid[i][j];
				// if soil cell has no affinity landplot or affinity landplot init list does not contain soil cell then make a new
				// land plot for the soil cell.
				if (oneSoilCell.getMyLandPlot() == null || !this.landPlots.contains(oneSoilCell.getMyLandPlot())
						|| ((oneSoilCell.getMyLandPlot().getAffinity() == CONCESSION_AFFINITY) && this.isRoomCell(
								oneSoilCell))) {
					if (isConcessionCell(oneSoilCell)) newPlot = new C_Concession(this);
					else if (this.isRoomCell(oneSoilCell)) newPlot = new C_Room(this);
					else newPlot = new C_LandPlot(this);
					context.add(newPlot);
					this.landPlots.add(newPlot);
					soilCell_0 = oneSoilCell;
					affinity0 = soilCell_0.getAffinity();
					// Je traite sc0
					soilCell_0.setAffinityLandPlot(newPlot);
					newPlot.setAffinity(affinity0);
					// And I detect and add to the newPlot all soilCells contiguous to this soilCell0 (sc0)
					// So I build completely this new landPlot before stating to build one other
					// la fileDattente CONTIENT LES SOILCELL QUI SONT DÉJÀ TRAITÉES :
					// Et dans cette list, les sc à partir de la position k
					// sont ceux dont les voisins ne sont pas encore taités
					fileDattente.clear();
					fileDattente.add(soilCell_0); // Seul sc0 est traité en ce moment
					fileDattente2.clear();
					fileDattente2.add(soilCell_0);
					k = 0;
					// Je reste sur le même landPlot pour le construire entièrement
					while (k < fileDattente.size()) {
						// On récupère un à un les soilCells déja traités, pour pouvoir localiser et traiter ses voisins
						soilCell_0 = fileDattente.get(k);
						x0 = soilCell_0.retrieveLineNo();
						y0 = soilCell_0.retrieveColNo();
						for (x = x0 - 1; x <= x0 + 1; x++) {
							for (y = y0 - 1; y <= y0 + 1; y++) { // avec ces 2 boucles j'accede à tous les 8 voisins de sc0
								if (!(x == x0 && y == y0) && (0 <= x && x < dimension_Ucell.getWidth() && 0 <= y
										&& y < dimension_Ucell.getHeight())) {
									soilCell_1 = (C_SoilCellUrban) grid[x][y]; // Pour chaque voisin scI de sc0, on teste :
									if (soilCell_1.getAffinity() == affinity0 && // si c'est de la même affinité que sc0 et
											!fileDattente2.contains(soilCell_1)) { // s'il n'est pas encore dans la file d'attente
										if ((this.isRoomCell(soilCell_1) || this.isConcessionCell(soilCell_1))
												&& (x != x0 && y != y0)) {
											C_SoilCellUrban soilCellTest_1 = (C_SoilCellUrban) grid[x][y0];
											C_SoilCellUrban soilCellTest_2 = (C_SoilCellUrban) grid[x0][y];
											if ((soilCellTest_1.getAffinity() == affinity0) || (soilCellTest_2
													.getAffinity() == affinity0)) {
												soilCell_1.setAffinityLandPlot(newPlot); // pour le traiter
												fileDattente.add(soilCell_1); // et l'ajoute dans la file d'attente pour étudier à
																				// son tour
												fileDattente2.add(soilCell_1);
											}
										}
										else {
											soilCell_1.setAffinityLandPlot(newPlot); // pour le traiter
											fileDattente.add(soilCell_1); // et l'ajoute dans la file d'attente pour étudier à son
																			// tour
											// chaqu'un de ses voisins
											fileDattente2.add(soilCell_1);
										}
									}
								}
							}
						}
						k++; // pour prendre l'élément suivant de la file d'attente
					}
				}
				// End of building a newlandPlot. We continue to build others
			}
		}
		A_Protocol.event("C_Landscape.identifyAffinityLandPlots(): ", landPlots.size() + " land plot(s) initialized",
				isNotError);
		return landPlots;
	}
	//
	// GETTERS
	//
	private boolean isConcessionCell(C_SoilCellUrban oneSoilCell) {
		return oneSoilCell.getAffinity() == CONCESSION_AFFINITY;
	}
	public boolean isRoomCell(C_SoilCellUrban oneCell) {
		switch (oneCell.getAffinity()) {
			case ROOM_AFFINITY :
			case SHOPFOOD_AFFINITY :
				// case MARKET_AFFINITY : // TODO MS de JLF 2022.05 Why ?
			case WORKSHOP_AFFINITY :
			case CORRIDOR_AFFINITY :
				return true;
		}
		return false;
	}
}