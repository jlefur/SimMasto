/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package simmasto0.protocol;
import java.util.Calendar;

import presentation.display.C_Background;
import repast.simphony.context.Context;
import simmasto0.C_ContextCreator;
import thing.C_Rodent;
import thing.C_RodentBlackRat;
import thing.C_RodentHouseMouse;
import thing.C_RodentMastoErySimple;
import thing.dna.C_GenomeAmniota;
import thing.dna.species.C_GenomeMastoErythroleucus;
import thing.dna.species.C_GenomeMusMusculus;
import thing.dna.species.C_GenomeRattusRattus;
import thing.ground.C_City;
import thing.ground.C_SoilCell;

import com.vividsolutions.jts.geom.Coordinate;

import data.C_Chronogram;
import data.C_Event;
import data.C_Parameters;
import data.constants.I_ConstantMusTransport;
import data.converters.C_ConvertGeographicCoordinates;

/** @author J.Le Fur, Mboup 07/2012, Version rev. P.A.Mboup, 08.2013, JLF 10.2015 */
public class C_ProtocolMusTransport extends A_ProtocolTransportation implements I_ConstantMusTransport {
	//
	// FIELDS
	//
	// protected C_InspectorGenetic geneticInspector;
	private C_ConvertGeographicCoordinates geographicCoordinateConverter = null;
	//
	// CONSTRUCTOR
	//
	public C_ProtocolMusTransport(Context<Object> ctxt) {
		super(ctxt);
		// geneticInspector = new C_InspectorGenetic();
		// inspectorList.add(geneticInspector);
		// C_CustomPanelSet.addGeneticInspector(geneticInspector);
		facilityMap = new C_Background(-3.05, 175, 125);
		// facilityMap = new C_Background(-3.1, 176, 125);
		chronogram = new C_Chronogram(CHRONO_FILENAME);
	}
	//
	// METHODS
	//
	@Override
	public void step_Utick() {
		// addRodentInTradingPost();
		super.step_Utick();
	}
	public void addRodentInTradingPost() {
		// Add rats randomly in trading post
		if ((protocolCalendar.get(Calendar.DAY_OF_MONTH) == 02) && (protocolCalendar.get(Calendar.YEAR) < 1981)) {
			C_City[] harbour = new C_City[9];
			harbour[0] = this.inspectorTransportation.getCityByName("Dakar");// 4 times to increase this city
			harbour[1] = this.inspectorTransportation.getCityByName("Dakar");
			harbour[2] = this.inspectorTransportation.getCityByName("Dakar");
			harbour[3] = this.inspectorTransportation.getCityByName("Saint-Louis");
			int randx = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * 4.);
			if (C_Parameters.VERBOSE)
				A_Protocol.event("C_ProtocolMusTransport.addRodentInTradingPost", "Added " + 10 + " rats from sea cargo or wreck in " + harbour[randx]
						+ ".", isNotError);
			addRodentsInCity(2, harbour[randx]);
		}
	}
	/** Initialize the protocol with the raster origin */
	public void initProtocol() {
		this.geographicCoordinateConverter = new C_ConvertGeographicCoordinates(new Coordinate(
				I_ConstantMusTransport.rasterLongitudeWest_LatitudeSouth_Udegree.get(0),
				I_ConstantMusTransport.rasterLongitudeWest_LatitudeSouth_Udegree.get(1)));
		this.initFixedParameters();
		super.initProtocol();
	}
	/** Author MSall 10.2015, rev. JLF 01.2017<br>
	 * @see A_Protocol#manageOneEvent */
	public void manageOneEvent(C_Event event) {
		Coordinate cellCoordinate = null;
		if (event.whereX_Ucell == null) {// then 1) suppose that y is also null, 2) double are values in decimal degrees
			// if (event.whereX_Udouble != null ) {
			cellCoordinate = this.geographicCoordinateConverter.convertCoordinate_Ucs(event.whereX_Udouble, event.whereY_Udouble);
			event.whereX_Ucell = (int) cellCoordinate.x;
			event.whereY_Ucell = (int) cellCoordinate.y;
		}
		int x = event.whereX_Ucell, y = event.whereY_Ucell;
		// JLF 09.2016 if a city is given coordinates in degrees in the chronogram, store it into the corresponding cell
		// bornCoordinate and use it to position the landplot (see A_ProtocolTransportation#createLandPlot
		if ((event.whereX_Udouble != 0) && (event.whereY_Udouble != 0)) {
			C_SoilCell eventSC = (C_SoilCell) this.landscape.getGrid()[x][y];
			eventSC.bornCoord_Umeter = cellCoordinate;
		}
		int nbrodents = 0;
		switch (event.type) {
			case RAT_EVENT :
				nbrodents = Integer.parseInt(event.value1);
				if (nbrodents > C_Parameters.RODENT_SUPER_AGENT_SIZE) nbrodents = nbrodents / C_Parameters.RODENT_SUPER_AGENT_SIZE;
				for (int i = 0; i < nbrodents; i++) {
					C_RodentBlackRat oneRodent = new C_RodentBlackRat(new C_GenomeRattusRattus());
					oneRodent.setAge_Uday(((C_GenomeAmniota) oneRodent.getGenome()).getSexualMaturity_Uday());
					C_SoilCell cell = (C_SoilCell) this.landscape.getGrid()[x][y];
					if (cell.getGroundTypes().contains(CITY_EVENT) || cell.getGroundTypes().contains(MARKET_EVENT))
						contextualizeNewThingInContainer(oneRodent, cell);
					else findCity(oneRodent, x, y);
				}
				break;
			case MASTO_ERY_EVENT :
				nbrodents = Integer.parseInt(event.value1);
				if (nbrodents > C_Parameters.RODENT_SUPER_AGENT_SIZE) nbrodents = nbrodents / C_Parameters.RODENT_SUPER_AGENT_SIZE;
				for (int i = 0; i < nbrodents; i++) {
					C_RodentMastoErySimple oneRodent = new C_RodentMastoErySimple(new C_GenomeMastoErythroleucus());
					C_SoilCell cell = (C_SoilCell) this.landscape.getGrid()[x][y];
					if (cell.getGroundTypes().contains(CITY_EVENT) || cell.getGroundTypes().contains(MARKET_EVENT))
						contextualizeNewThingInContainer(oneRodent, cell);
					else findCity(oneRodent, x, y);
				}
				break;
			case MUS_EVENT :
				nbrodents = Integer.parseInt(event.value1);
				if (nbrodents > C_Parameters.RODENT_SUPER_AGENT_SIZE) nbrodents = nbrodents / C_Parameters.RODENT_SUPER_AGENT_SIZE;
				for (int i = 0; i < nbrodents; i++) {
					C_RodentHouseMouse oneRodent = new C_RodentHouseMouse(new C_GenomeMusMusculus());
					C_SoilCell cell = (C_SoilCell) this.landscape.getGrid()[x][y];
					if (cell.getGroundTypes().contains(CITY_EVENT) || cell.getGroundTypes().contains(MARKET_EVENT))
						contextualizeNewThingInContainer(oneRodent, cell);
					else findCity(oneRodent, x, y);
				}
				break;
		}
		super.manageOneEvent(event);
	}
	private void findCity(C_Rodent oneRodent, int x, int y) {
		boolean found = false;
		C_SoilCell cell;
		int range = 0;
		while (!found) {
			range++;
			for (int j = Math.max(0, x - range); j < Math.min(this.landscape.getDimension_Ucell().getWidth(), x + range); j++) {
				if (found) break;
				for (int k = Math.max(0, y - range); k < Math.min(this.landscape.getDimension_Ucell().getHeight(), y + range); k++) {
					if (found) break;
					cell = (C_SoilCell) this.landscape.getGrid()[j][k];
					if (cell.getGroundTypes().contains(CITY_EVENT) || cell.getGroundTypes().contains(MARKET_EVENT)) {
						contextualizeNewThingInContainer(oneRodent, cell);
						found = true;
						if (C_Parameters.VERBOSE)
							A_Protocol.event("C_ProtocolMusTransport.findCity", oneRodent + " at " + x + "," + y + " asked; found " + cell
									+ " at range " + range, isNotError);
					}
				}
			}
		}
	}

	@Override
	/** TODO JLF 2016.03 the marketDay value is temporary stored in the affinity field, before being passed to the the city
	 * landplot. */
	protected void manageGroundEvent(C_Event event) {
		int x = event.whereX_Ucell, y = event.whereY_Ucell;
		C_SoilCell eventSC = (C_SoilCell) this.landscape.getGrid()[x][y];// SC for SoilCell
		if (event.type.equals(MARKET_EVENT)) {
			int marketDay = Integer.parseInt(event.value2.substring(0, 1));
			eventSC.setAffinity(marketDay);
			event.type = CITY_EVENT;// a market cell has to be managed as a city cell
			eventSC.getGroundTypes().add(MARKET_EVENT);
		}
		super.manageGroundEvent(event);
	}
	@Override
	/** Randomly create black rat or house mouse
	 * @see manageOneEvent */
	public C_Rodent createRodent() {
		if (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() > .5) // TODO number in source 2017.01 JLF
			return new C_RodentBlackRat(new C_GenomeRattusRattus());
		else return new C_RodentHouseMouse(new C_GenomeMusMusculus());
	}
	@Override
	public void initCalendar() {
		protocolCalendar.set(2014, Calendar.NOVEMBER, 1);
	}
	@Override
	public String toString() {
		return "protocolMusTransport";
	}
}