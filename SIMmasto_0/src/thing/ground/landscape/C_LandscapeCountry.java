package thing.ground.landscape;

import repast.simphony.context.Context;
import thing.ground.C_SoilCellGraphed;
import data.constants.I_ConstantTransportation;

/** Tag C_soilCellGraphed inside or outside domain Author: PAM 2013, rev. jlf 10.2015 */
public class C_LandscapeCountry extends C_LandscapeNetwork implements I_ConstantTransportation {
	//
	// CONSTRUCTOR
	//
	public C_LandscapeCountry(Context<Object> context, String url, String gridValueName, String continuousSpaceName) {
		super(context, url, gridValueName, continuousSpaceName);
		this.graphTypes = I_ConstantTransportation.GRAPH_TYPES;
		this.areaTypes = I_ConstantTransportation.AREA_TYPES;
	}
	//
	// METHOD
	//
	/** Tag C_soilCellGraphed inside or outside domain <br>
	 * Author: PAM 2013, rev. jlf 10.2015 */
	@Override
	public void createGround(int[][] matriceLue) {
		super.createGround(matriceLue);
		for (int i = 0; i < this.dimension_Ucell.width; i++) {
			for (int j = 0; j < this.dimension_Ucell.height; j++) {
				// set cells and value layer as outside or inside domain
				if (!(matriceLue[i][j] == AFFINITY_OUTSIDE_DOMAIN)) {
					((C_SoilCellGraphed) this.grid[i][j]).addGroundType(GROUNDTYPE_INSIDE_DOMAIN + "");
					this.graphedValueLayer.set(GROUNDTYPE_INSIDE_DOMAIN, i, j);
				}
				else {
					((C_SoilCellGraphed) this.grid[i][j]).addGroundType(GROUNDTYPE_OUTSIDE_DOMAIN + "");
					this.graphedValueLayer.set(GROUNDTYPE_OUTSIDE_DOMAIN, i, j);
				}
			}
		}
	}
}
