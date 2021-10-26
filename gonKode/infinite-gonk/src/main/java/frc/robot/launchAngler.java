package frc.robot;


import com.revrobotics.ControlType;

public class launchAngler {
    limelight ranger = new limelight(); //names limelight ranger in this class
    double atRange = -1;
    public double positionTarget;
    
    public void tip(){                  //method name tip
        atRange = ranger.rangeFinder();
        angleSet(Robot.targetAngle);
        double a = 0.000424855;
        double b = -0.216901;
        double c = 57.8328;


        Robot.targetAngle = a*Math.pow(atRange, 2) + b*(atRange) + c ;

        if(Robot.ext.getRawAxis(0) < 0.05 && Robot.ext.getRawAxis(0) > -0.05)
        {
            Robot.pitcherPID.setReference(0, ControlType.kVelocity);
        }
        else
        {
            Robot.pitcher.set(Robot.ext.getRawAxis(0)*0.25);
        }
        //shooting sections:
        //
        // if(ranger.rangeFinder() > 70 && ranger.rangeFinder() < 300)
        //{
        //      if(ranger.rangeFinder()>=110 && ranger.rangeFinder()<170)
        //             {
        //                 Robot.targetAngle = 0.000557516*Math.pow(atRange, 2) +- 0.29449*(atRange) + 66.2420 ; //c was 66.2420
        //             }
        //      else if(ranger.rangeFinder()>=170 && ranger.rangeFinder()<220)
        //             {
        //                 Robot.targetAngle = 0.000557516*Math.pow(atRange, 2) +- 0.29449*(atRange) + 66.7420 ; //c was 66.7420
        //             }
        //      else if(ranger.rangeFinder()>=220 && ranger.rangeFinder()<300)
        //             {
        //                 Robot.targetAngle = 0.000557516*Math.pow(atRange, 2) +- 0.29449*(atRange) + 66.2420 ; //c was 66.2420
        //             }
        //      else 
        //             {
        //                 Robot.targetAngle = 0.000557516*Math.pow(atRange, 2) +- 0.29449*(atRange) + 66.2420 ; //c was 66.2420
        //             }       
        //     //original
        //     Robot.targetAngle = 0.000557516*Math.pow(atRange, 2) +- 0.29449*(atRange) + 66.2420 ; 
        //     Robot.targetAngle = 0.0006922*Math.pow(atRange, 2)+-0.24+2*(atRange)+56;
        // }  
    }
    
    public void angleSet(double targetAngle){       //method name angleSet
        positionTarget = (targetAngle-50)/(-5.07);

        if(Robot.pitchEnc.getPosition() > positionTarget + 1)
        {
            Robot.pitcher.set(-0.25);
        }
        else if(Robot.pitchEnc.getPosition() < positionTarget - 1)
        {
            Robot.pitcher.set(0.25);
        }
        else if(Robot.pitchEnc.getPosition() < positionTarget)
        {
            Robot.pitcher.set(.2*Math.pow(Math.abs(Robot.pitchEnc.getPosition()-positionTarget), 0.9));
        }
        else if(Robot.pitchEnc.getPosition() > positionTarget)
        {
            Robot.pitcher.set(-.2*Math.pow(Math.abs(Robot.pitchEnc.getPosition()-positionTarget), 0.9));
        }
        else
        {
            Robot.pitcherPID.setReference(0, ControlType.kVelocity);
        }

        if(Robot.pitchEnc.getPosition() < positionTarget + 0.2 || Robot.pitchEnc.getPosition() > positionTarget - 0.2)
        {
            Robot.launchStatus = true;
        }
    }

    public void innerSet(){     //method name innerSet
       // double innerHoleAngle = 21;
        angleSet(21);
    }




}
