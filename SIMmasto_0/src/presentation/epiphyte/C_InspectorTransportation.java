/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package presentation.epiphyte;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import presentation.dataOutput.C_FileWriter;
import repast.simphony.engine.environment.RunState;
import repast.simphony.essentials.RepastEssentials;
import simmasto0.protocol.A_Protocol;
import thing.A_NDS;
import thing.C_HumanCarrier;
import thing.C_Rodent;
import thing.I_SituatedThing;
import thing.ground.C_City;
import thing.ground.C_LandPlot;
import data.C_Parameters;
import data.constants.I_ConstantCentenal;
import data.constants.I_ConstantString;
/** From case study number 3 - Senegal Centenal M2 P.A.Mboup
 * @author J.Le Fur & Mboup 07/2012, rev. JLF 09.2014 */
public class C_InspectorTransportation extends A_Inspector implements I_ConstantString, I_ConstantCentenal {
	//
	// FIELDS
	//
	/** Periodically discharged in the output file */
	private List<String> outputBufferRodentPop;
	private List<String> outputBufferRodentRate;
	private List<String> outputBufferHumanPop;
	protected C_FileWriter rodentPop;
	protected C_FileWriter rodentRates;
	protected C_FileWriter humanPop;
	/** The landplots list is updated by adds and removes @see cityList */
	protected TreeSet<C_LandPlot> landPlotList;
	/** The cities list is fully renewed/recomputed @see computeCityList @see lanplotList */
	protected TreeSet<C_City> cityList;
	protected TreeSet<C_HumanCarrier> carrierList;
	//
	// CONSTRUCTORS
	//
	public C_InspectorTransportation(TreeSet<C_LandPlot> landPlotInitList) {
		this();
		this.landPlotList = landPlotInitList;
	}
	public C_InspectorTransportation() {
		super();
		indicatorsHeader = "Tick;Loaded Rodents;pregnant transported;N vehicles";
		this.landPlotList = new TreeSet<C_LandPlot>();
		this.carrierList = new TreeSet<C_HumanCarrier>();
		this.cityList = new TreeSet<C_City>();
		// Data output
		String outputFileName = "-" + C_Parameters.RODENT_SUPER_AGENT_SIZE + "RATS-" + C_Parameters.HUMAN_SUPER_AGENT_SIZE + "HC";
		this.outputBufferRodentRate = new ArrayList<String>();
		this.outputBufferRodentPop = new ArrayList<String>();
		this.outputBufferHumanPop = new ArrayList<String>();
		this.rodentPop = new C_FileWriter(outputFileName + "_rodentPop.csv", true);
		this.rodentPop.closeFile();
		this.rodentRates = new C_FileWriter(outputFileName + "_rodentRate.csv", true);
		this.rodentRates.closeFile();
		this.humanPop = new C_FileWriter(outputFileName + "_humanPop.csv", true);
		this.humanPop.closeFile();
	}
	//
	// METHODS
	//
	/** Do super() then add a data line to csv file<br>
	 * JLF 12.2015
	 * @see A_Protocol#step_Utick */
	@Override
	public void step_Utick() {
		super.step_Utick();
		if ((A_Protocol.protocolCalendar.get(Calendar.MONTH) == Calendar.JANUARY) && (A_Protocol.protocolCalendar.get(Calendar.DAY_OF_MONTH) == 01)) this
				.addOutputDataLineForRodent();
	}
	/** Remove carriers or city or landplot from this inspector's lists */
	@Override
	public void discardDeadThing(I_SituatedThing thing) {
		boolean test = true;
		if ((thing instanceof C_HumanCarrier) && !this.carrierList.remove(thing)) test = false;
		else if ((thing instanceof C_City) && !this.cityList.remove(thing)) test = false;
		else if ((thing instanceof C_LandPlot) && !this.landPlotList.remove(thing)) test = false;
		if (!test) A_Protocol.event("C_InspectorTransportation.discardDeadThing", "Could not remove " + ((A_NDS) thing).retrieveMyName(), isError);
	}
	public void checkLandPlotLists() {
		boolean ok = true;
		Object[] contextContent = RunState.getInstance().getMasterContext().toArray();
		TreeSet<C_LandPlot> context_Ulandplot = new TreeSet<C_LandPlot>();
		for (int i = 0; i < contextContent.length; i++) {
			if (contextContent[i] instanceof C_LandPlot) context_Ulandplot.add((C_LandPlot) contextContent[i]);
		}
		for (C_LandPlot landplot : this.landPlotList)
			if (!context_Ulandplot.contains(landplot)) {
				ok = false;
				A_Protocol.event("C_InspectorTransportation.checkLandPlotLists : ", landplot + "landplot does not exist in context", isError);
			}
		for (C_LandPlot landplot : context_Ulandplot)
			if (!this.landPlotList.contains(landplot) && (landplot.getPlotType() != null)) // affinity landPlot type is null and
																							// not
																							// accounted here
			{
				ok = false;
				System.err.println("checkLandPlotList: " + landplot + "landplot does not exist in inspector's list");
			}
		if (!ok) System.err.println("C_InspectorTransportation.checkLandPlotList ok" + this.landPlotList.size() + " landplots");
	}
	//
	// DATA OUTPUT
	//
	@Override
	/** stores the current state of indicators in the field including the super ones has to be conform with indicatorsHeader / JLF
	 * 06.2016 */
	public String indicatorsStoreValues() {
		indicatorsValues = RepastEssentials.GetTickCount() + CSV_FIELD_SEPARATOR + getCarriersLoad_Urodent() + CSV_FIELD_SEPARATOR + getPregnantLoad()
				+ CSV_FIELD_SEPARATOR + getCarrierList().size();
		return (indicatorsValues);
	}
	/** This method avoid to open, write and close a rodent output data file in each step of the simulation\n It just open, write and close the file
	 * when the size of the list outputBufferRodentRate reach OUTPUT_BUFFER_SIZE by flushAndCloseOutputBuffer()
	 * @see #flushAndCloseOutputBuffer(C_FileWriter, List) */
	public void addOutputDataLineForRodent() {
		if (!this.cityList.isEmpty()) {
			double rodentRate = 0;
			String lineAndTick = A_Protocol.protocolCalendar.stringShortDate() + CSV_FIELD_SEPARATOR + RepastEssentials.GetTickCount();
			for (C_City oneCity : this.cityList) {
				if (oneCity.getHumanPopSize_Uindividual() != 0) rodentRate = (double) oneCity.getFullLoad_Urodent()
						* (double) C_Parameters.RODENT_SUPER_AGENT_SIZE * 1000. / (double) oneCity.getHumanPopSize_Uindividual();
				else rodentRate = 0.; // oneCity is uninhabited
				lineAndTick += CSV_FIELD_SEPARATOR + rodentRate;
			}
			outputBufferRodentRate.add(lineAndTick);

			lineAndTick = A_Protocol.protocolCalendar.stringShortDate() + CSV_FIELD_SEPARATOR + RepastEssentials.GetTickCount();
			for (C_City oneCity : this.cityList)
				lineAndTick += CSV_FIELD_SEPARATOR + oneCity.getFullLoad_Urodent();
			outputBufferRodentPop.add(lineAndTick);

			lineAndTick = A_Protocol.protocolCalendar.stringShortDate() + CSV_FIELD_SEPARATOR + RepastEssentials.GetTickCount();
			for (C_City oneCity : this.cityList)
				lineAndTick += CSV_FIELD_SEPARATOR + oneCity.getHumanPopSize_Uindividual();
			outputBufferHumanPop.add(lineAndTick);

			if (outputBufferRodentRate.size() >= OUTPUT_BUFFER_SIZE) {
				flushAndCloseOutputBuffer(rodentRates, outputBufferRodentRate);
				flushAndCloseOutputBuffer(rodentPop, outputBufferRodentPop);
				flushAndCloseOutputBuffer(humanPop, outputBufferHumanPop);
			}
		}
	}
	public void resetCityToOutputHeader(List<String> outputDataList) {
		String lineAndTick = A_Protocol.protocolCalendar.stringShortDate() + ";Tick";
		for (C_City oneCity : this.cityList)
			lineAndTick += CSV_FIELD_SEPARATOR + oneCity.retrieveMyName();
		outputDataList.add(lineAndTick);
	}
	/** @see #addOutputDataLineForRodent() */
	public void flushAndCloseOutputBuffer(C_FileWriter transportationIndicators, List<String> outputDataInternalBuffer) {
		C_FileWriter.writeMultiLineAndClose(transportationIndicators.getName(), outputDataInternalBuffer);
		outputDataInternalBuffer.clear();
	}
	/** close private files */
	public void closeSimulation() {
		flushAndCloseOutputBuffer(rodentRates, outputBufferRodentRate);
		flushAndCloseOutputBuffer(rodentPop, outputBufferRodentPop);
		flushAndCloseOutputBuffer(humanPop, outputBufferHumanPop);
	}
	//
	// SETTERS & GETTERS
	//
	public void addCarrier(C_HumanCarrier carrier) {
		this.carrierList.add(carrier);
	}
	public void addLandPlot(C_LandPlot oneNewLandPlot) {
		this.landPlotList.add(oneNewLandPlot);
	}
	/** Compute the whole list; supposes the landPlot list is up to date and reset city output header<br>
	 * author J.Le Fur 09.2014 */
	public void renewCityList() {
		cityList.clear();
		for (C_LandPlot region : this.landPlotList) {
			if (region instanceof C_City) cityList.add((C_City) region);
		}
		resetCityToOutputHeader(outputBufferRodentRate);
		resetCityToOutputHeader(outputBufferRodentPop);
		resetCityToOutputHeader(outputBufferHumanPop);
	}
	/** @param groundType : extract only landPlots of this type
	 * @return a subset of landPlots with all landPlots of the same type. author J.Le Fur, 09.2014 */
	public TreeSet<C_LandPlot> getLandPlotList(String groundType) {
		TreeSet<C_LandPlot> landPlotsOfAType = new TreeSet<C_LandPlot>();
		for (C_LandPlot landPlot : this.landPlotList) {
			if (landPlot.getPlotType().equals(groundType)) landPlotsOfAType.add(landPlot);
		}
		return landPlotsOfAType;
	}
	public C_City getCityByName(String name) {
		for (C_City city : cityList)
			if (city.retrieveMyName().contains(name)) return city;
		return null;
	}
	public int getCarriersLoad_Urodent() {
		int rodentLoad = 0;
		for (C_HumanCarrier oneCarrier : carrierList)
			rodentLoad += oneCarrier.getVehicle().getLoad_Urodent();
		return rodentLoad;
	}
	/** Count pregnant female tranported by vehicles; author JLF 06.2016 */
	public int getPregnantLoad() {
		int pregnantLoad = 0;
		for (C_Rodent one_rodent : C_InspectorPopulation.rodentList)
			if (one_rodent.isPregnant() && one_rodent.isTrappedOnBoard()) pregnantLoad++;
		return pregnantLoad;
	}
	public TreeSet<C_HumanCarrier> getCarrierList() {
		return carrierList;
	}
	public Set<C_LandPlot> getLandPlotList() {
		return landPlotList;
	}
	public TreeSet<C_City> getCityList() {
		return cityList;
	}
}