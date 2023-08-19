/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing;

import thing.ground.C_LandPlot;
import thing.ground.I_Container;
import com.vividsolutions.jts.geom.Coordinate;

/** Any object or agent in a World is a Thing. They all share some basic properties, so these are implemented as an interface.
 * @author Jean Le Fur 2007 (from Kyle Wagner-cricketSim, elyk@acm.org 2000) Version 1.0, Sun Nov 12 00:56:39 2000, rev jlf
 *         03.2015, 02.2017, 09.2020
 * @see A_NDS
 * @see A_VisibleAgent */

public interface I_SituatedThing {

	public Coordinate getCoordinate_Umeter();
	public Coordinate getCoordinate_Ucs();
	public void setCurrentSoilCell(I_Container cell);
	public I_Container getCurrentSoilCell();
	public void discardThis();
	public double getDistance_Umeter(Coordinate oneCoordinate);
	public double getDistance_Umeter(I_SituatedThing agent);
	public void assertColNo(int colNo);
	public void assertLineNo(int lineNo);
	public int retrieveLineNo();
	public int retrieveColNo();
	/** Transmit infection to thing, has to be overridden in daughter classes */// TODO MS de JLF: ne concerne pas tous les
																				// scénarios
	public void actionInfect(I_SituatedThing thing);
	public boolean isInfected();
	public boolean canInteractWith(I_SituatedThing oneOccupant);// JLF 03.2021
	
	public C_LandPlot getMyLandPlot();// JLF 01.2022
}
