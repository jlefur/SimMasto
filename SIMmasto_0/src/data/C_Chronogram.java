/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package data;
import java.io.BufferedReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import data.constants.I_ConstantGerbil;
import data.constants.I_ConstantNumeric;
import data.constants.I_ConstantString;
import simmasto0.C_Calendar;
import simmasto0.protocol.A_Protocol;
/** Builds a data chrono from a CSV events file
 * @author pamboup 21/06/2013, rev JLF 08.2014 */
public class C_Chronogram implements I_ConstantString, I_ConstantNumeric, I_ConstantGerbil {
    //
    // FIELDS
    //
    /** Contains the data from the chrono */
    private ArrayList<String> fullEvents_Ustring = new ArrayList<String>();
    /** current index within the chrono */
    private int chronoCurrentIndex = 0;
    /** number of lines of the chrono */
    private int chronoLength = 0;
    /** The current date from the chrono, Different from current date of the simulation<br>
     * If dataChronoCurrentDate == current date of simulation then we have to manage the simulation from the chrono */
    private Date chronoCurrentDate;
    public boolean isEndOfChrono = false;
    //
    // CONSTRUCTOR
    //
    /** Retrieve data from a chronogram, put the events of dataChrono array field to allow a direct access and to close the buffer <br>
     * The internal chronogram date is in format : dd/mm/yyyy.<br>
     * Chronogram format : DATE_COL, X_COL, Y_COL, VALUE1_COL, VALUE2_COL ...<br>
     * CSV semicolon separator (;) The csv chronogram file must be in data_csv directory/
     * @see I_ConstantNumeric author pamboup 21/06/2013, rev. JLF 08.2014 */
    public C_Chronogram(String csvChronoName) {
        A_Protocol.event("C_Chronogram.C_Chronogram()", "Chronogram file name : " + csvChronoName, isNotError);
        BufferedReader buffer = C_ReadWriteFile.openBufferReader(CSV_PATH, csvChronoName);
        String readLine;
        try {
            readLine = buffer.readLine();// reads the header (column names)
            readLine = buffer.readLine();// reads the first line
            if (readLine != null) {
                do {// put the entire chrono in dataChrono
                    this.fullEvents_Ustring.add(readLine);
                } while ((readLine = buffer.readLine()) != null); // reads the next line
                this.chronoLength = this.fullEvents_Ustring.size();
                // TODO MS 11.2020 Temporary modification off the setting current date
                SimpleDateFormat dateFormat = new SimpleDateFormat("DD/MM/YYYY");
                String currentDate = this.fullEvents_Ustring.get(0).split(CSV_FIELD_SEPARATOR)[DATE_COL];
                try {
                    dateFormat.parse(currentDate);
                    this.setChronoCurrentDate(currentDate);
                    System.out.println("C_Chronogram.constructor(): " + chronoLength + " events loaded in memory");
                }catch(Exception e) {
                    System.err.println("C_Chronogram.C_Chronogram() : Human activity chronogram!"+ e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("C_ReadWriteFile.chronobuilderFromCsvFile : error when retrieving the chrono events " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            try {
                buffer.close();
            } catch (Exception e) {
                System.err.println("C_ReadWriteFile.chronobuilderFromCsvFile : " + "buffer or rasterFile closing error" + e.getMessage());
            }
        }
    }
    //
    // METHODS
    //
    /** Compare dates, retrieve genuine events, process the string stuff, fill in a treeSet of C_Event.
     * @return a tree set with the current events to process */
    public List<C_Event> retrieveCurrentEvents(Date simulationCurrentDate) {
        List<C_Event> eventsRead = null;
        // Read chrono as long as chrono date <= simulation date
        if (this.getChronoCurrentDate().equals(simulationCurrentDate)//
                || (this.getChronoCurrentDate().before(simulationCurrentDate))) {
            eventsRead = new ArrayList<C_Event>();
            int chronoIndex = this.getCurrentIndex();
            // contains all manipulated events name which implies to update environment after change
            while ((this.getChronoCurrentDate().equals(simulationCurrentDate)) || //
                    (this.getChronoCurrentDate().before(simulationCurrentDate))) {// in case tick unit is greater than chrono time
                                                                                  // step unit
                // specific management of daughter protocols
                eventsRead.add(new C_Event(this.fullEvents_Ustring.get(chronoIndex)));
                chronoIndex++;
                // The first value of chronoIndex is zero
                if (chronoIndex >= (this.chronoLength)) { // mark chrono as terminated
                    chronoIndex -= 1;
                    this.isEndOfChrono = true;
                    break;
                }
                this.setChronoCurrentDate(this.fullEvents_Ustring.get(chronoIndex).split(CSV_FIELD_SEPARATOR)[DATE_COL]);
            }
            this.setCurrentIndex(chronoIndex);
        }
        return eventsRead; // return null if it is not time to read chrono
    }
    //
    // SETTERS & GETTERS
    //
    public void setCurrentIndex(int dataChronoCurrentIndex) {
        this.chronoCurrentIndex = dataChronoCurrentIndex;
    }
    public void setChronoCurrentDate(String currentDate) {
        try {
            this.chronoCurrentDate = C_Calendar.shortDatePattern.parse(currentDate);
        } catch (ParseException e) {
            System.err.println("C_Chronogram.setChronoCurrentDate: unable to parse " + currentDate);
            e.printStackTrace();
        }
    }
    public int getChronoLength() {
        return this.chronoLength;
    }
    public ArrayList<String> getFullEvents_Ustring() {
        return this.fullEvents_Ustring;
    }
    public int getCurrentIndex() {
        return this.chronoCurrentIndex;
    }
    public Date getChronoCurrentDate() {
        return this.chronoCurrentDate;
    }
}