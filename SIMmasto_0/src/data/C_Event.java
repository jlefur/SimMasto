/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package data;
import java.text.ParseException;
import java.util.Date;

import presentation.dataOutput.C_Information;
import simmasto0.C_Calendar;
import simmasto0.C_ContextCreator;
import simmasto0.protocol.A_Protocol;
import data.constants.I_ConstantNumeric;
import data.constants.I_ConstantString;

/** WORK IN PROGRESS Basic structure to communicate between worlds (aims to be related with C_Information ?)
 * @see C_Information
 * @author Jean Le Fur (lefur@ird.fr) Version 04/06/2014, rev.09.2014, JLF & M.Sall 11.2015 */
public class C_Event implements Comparable<C_Event>, I_ConstantNumeric, I_ConstantString {
    //
    // FIELDS
    //
    public Date when_Ucalendar;
    public Integer whereX_Ucell;
    public Integer whereY_Ucell;
    public double whereX_Udouble;
    public double whereY_Udouble;
    public String type;
    public String value1;// can be parsed to a number by the user protocol
    public String value2;// can be parsed to a list by the user protocol (the first value is a tag: "city" or one of GROUND_TYPE_AREA
    public String otherValues;// TODO MS 2020.10 add to retrieve the others values of the event
    private String myId = null;// unique identifiers in the project must be strings (event if they represent numbers, cf. CI)
    //
    // CONSTRUCTOR
    //
    /** instantiate an event with geographic coordinates or cell coordinate */
    protected C_Event(String readLine) {
        String[] readArray = readLine.split(CSV_FIELD_SEPARATOR);
        // Set date alone since it must be embedded in try/catch
        try {
            this.when_Ucalendar = C_Calendar.shortDatePattern.parse(readArray[DATE_COL].trim());
        } catch (ParseException e) {
            System.err.println("C_Chronogram.createEventFromString() unable to parse " + readArray[DATE_COL].trim());
            e.printStackTrace();
        }
        this.type = readArray[EVENT_COL];
        this.value1 = readArray[VALUE1_COL];
        this.value2 = readArray[VALUE2_COL];
        if (readArray.length > VALUE2_COL + 1) {
            int i = VALUE2_COL + 1;
            this.otherValues = readArray[i++];
            try {
                while (i < readArray.length) {
                    this.otherValues += ";" + readArray[i];
                    i++;
                }
            } catch (Exception e) {
                System.out.println("C_Event.C_Event()");
            }
        }
        if (readArray[X_COL].length() != 0 && readArray[Y_COL].length() != 0) {
            if (readArray[X_COL].contains(".") || readArray[Y_COL].contains(".")) {
                // case when x and y are in degrees, create an event using double coordinates
                this.whereX_Udouble = Double.parseDouble(readArray[X_COL]);
                this.whereY_Udouble = Double.parseDouble(readArray[Y_COL]);
            }
            else {
                this.whereX_Ucell = Integer.parseInt(readArray[X_COL]);
                this.whereY_Ucell = Integer.parseInt(readArray[Y_COL]);
                this.whereX_Udouble = this.whereX_Ucell;
                this.whereY_Udouble = this.whereY_Ucell;
            }
        }
        this.myId = String.valueOf(C_ContextCreator.EVENT_NUMBER);
        C_ContextCreator.EVENT_NUMBER++;
        if (C_Parameters.VERBOSE) A_Protocol.event("C_Event.C_Event()", this.toString(), isNotError);
    }
    //
    // METHODS
    //
    public int compareTo(C_Event other) {
        return other.myId.compareTo(this.myId);
    }
    @Override
    public String toString() {
//        if (this.whereX_Ucell == null) // suppose if x null, then y null also
//            return "EVENT [" + C_Calendar.shortDatePattern.format(when_Ucalendar) + "(" + whereX_Udouble + "," + whereY_Udouble + ")" + "_" + type + "_"
//                    + value1 + "_" + value2 + "]";
//        else
            return "EVENT [" + whereX_Ucell + "," + whereY_Ucell + "]" + ", " + type + ", "
                    + value1 + ", " + value2;
    }
}
