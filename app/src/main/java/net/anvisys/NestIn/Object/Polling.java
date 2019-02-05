package net.anvisys.NestIn.Object;

import java.io.Serializable;

/**
 * Created by Amit Bansal on 11-04-2016.
 */
public class Polling implements Serializable{

    public int PollID;
    public String Question;
    public String Answer1;
    public String Answer2;
    public String Answer3;
    public String Answer4;
    public int Answer1Count;
    public int Answer2Count;
    public int Answer3Count;
    public int Answer4Count;
    public int previousSelected;
    public String Start_Date;
    public String End_Date;
}
