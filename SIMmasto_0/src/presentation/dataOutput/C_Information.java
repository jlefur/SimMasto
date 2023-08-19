/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package presentation.dataOutput;

import java.util.Date;
import data.C_Event;

/**  UNUSED: WORK IN PROGRESS
 * Exploratory: Structure compatible with the Centre d'Informations (CI). Aims at receiving data to include in the CI <br>
 * NB: lack keywords.
 * @see C_Event
 * Version 04/06/2014, rev. JLF 12.2014, 08.2018 
 * @author Jean Le Fur (lefur@ird.fr) */
public class C_Information {

	public String idInformation;
	public String title;// message
	public String subTitle;// method concerned (i.e., A_Animal.step_Utick)
	public String shortDescription;
	public String dimension;// "simulation output"
	public String source;// protocol ?
	public String author;//i.e. this (?)
	public Date entryDate;// simulation date or today date ?

	public C_Information(String idInformation, String title, String subTitle, String shortDescription,
			String dimension, String source, String author, Date entryDate) {
		super();
		this.idInformation = idInformation;
		this.title = title;
		this.subTitle = subTitle;
		this.shortDescription = shortDescription;
		this.dimension = dimension;
		this.source = source;
		this.author = author;
		this.entryDate = entryDate;
	}

}