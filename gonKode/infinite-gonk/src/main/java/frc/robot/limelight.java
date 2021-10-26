package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class limelight{    
  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  NetworkTableEntry tx = table.getEntry("tx");
  NetworkTableEntry ty = table.getEntry("ty");
  NetworkTableEntry tv = table.getEntry("tv");
  NetworkTableEntry ta = table.getEntry("ta");
  NetworkTableEntry ts = table.getEntry("ts");
  NetworkTableEntry tvert = table.getEntry("tvert");
  NetworkTableEntry tlong = table.getEntry("tlong");
  NetworkTableEntry tshort = table.getEntry("tshort");
  NetworkTableEntry ledMode = table.getEntry("ledMode");
  NetworkTableEntry camMode = table.getEntry("camMode");
  NetworkTableEntry stream = table.getEntry("stream");
  NetworkTableEntry pipeline = table.getEntry("pipeline");
  // NetworkTableEntry dashxOff = Limit.add("xOffset",0.0).getEntry();
  // NetworkTableEntry dashTarget = Limit.add("Target",false).getEntry();

  public double xTranslate;
  public double yTranslate;
  public double yOffLimit = -509;
  double distance;
  public boolean trueTarget;
  public int isTarget;

  public void camControl(){
    pipeline.setDouble(0);
    isTarget = (int)tv.getDouble(0);
    xTranslate = tx.getDouble(0.0);
    yTranslate = ty.getDouble(0);
    trueTarget = true;

    if(isTarget != 0)
    {
      if(xTranslate > offsetCalculator() + 1) //was + 0.1
      { 
        Robot.scoot.driveCartesian(Robot.logi1.getRawAxis(0), -Robot.logi1.getRawAxis(1), 0.25*Math.pow((xTranslate + offsetCalculator())/57, 0.9));
      }
      else if(xTranslate < offsetCalculator() - 1)  //was - 0.1
      { 
        Robot.scoot.driveCartesian(Robot.logi1.getRawAxis(0), -Robot.logi1.getRawAxis(1), -0.25*Math.pow(-(xTranslate - offsetCalculator())/57, 0.9));
      }
      else
      {
        Robot.scoot.driveCartesian(Robot.logi1.getRawAxis(0), -Robot.logi1.getRawAxis(1), Robot.logi.getRawAxis(0)); 
      }
    }
    else
    {
      Robot.scoot.driveCartesian(Robot.logi1.getRawAxis(0), -Robot.logi1.getRawAxis(1), Robot.logi.getRawAxis(0));
    }
  }

  public double offsetCalculator(){
    double inchOffRobot = 8.75; //was 8.5
    double offset = Math.atan(inchOffRobot/(rangeFinder()));
    offset = (offset * 180)/Math.PI;
    return offset;
  }
    
  public double rangeFinder(){
    isTarget = (int)tv.getDouble(0);
    double heightFloor = 23;//originally 23
    //double llOffset = 8.25;//originally 8.25    note-not used
    double llAngle = 27;//originally 27
    double targetHeight = 91.125; //originally 91.125
    double yOff = ty.getDouble(0.0);
    //double xOff = tx.getDouble(0.0);
    if (isTarget != 0)
    {
      distance = (int)((targetHeight-heightFloor)/Math.tan((yOff + llAngle) * ((2 * Math.PI)/360)));
      // distance = distance - (llOffset * Math.sin(Math.abs(xOff) * ((2 * Math.PI)/360)));
    }
    else
    {
      distance = -1;
    }

    return distance + 29;
  }   
}
