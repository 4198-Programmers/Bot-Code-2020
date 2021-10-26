package frc.robot;

public class colorParse{
    String colorOutput(double cBlue, double cGreen, double cRed){
        // double redBlue = cRed/cBlue;
        // double redGreen = cRed/cGreen;
        // double blueGreen = cBlue/cGreen; 

        double blueRed = cBlue/cRed;
        
        // double greenRed = cGreen/cRed;
        // double greenBlue = cGreen/cBlue;

        // System.out.println(blueRed + " blueRed");
        // System.out.println(greenRed + " greenRed");
        // System.out.println(greenBlue + " greenBlue");

        if(cGreen > cRed && blueRed > 2){
            // System.out.println("BLUE");
            return "B";
        }
        else if(cGreen > cBlue && cGreen > cRed){
            if(cRed > cBlue){
                return "Y";
            }
            else{
                return "G";
            }
        }
        else if(cGreen > cBlue && cGreen < cRed ) {
            return "R";
        }
        else{
            return "oops";
        }
    }
}
