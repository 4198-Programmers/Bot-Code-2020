package frc.robot;

public class climb{
    
    void winch(){                               //method name winch
        if(Robot.ext.getRawButton(4))           //when ext(4) hook out
        {
            Robot.fricWheel.set(-1);            
        }
        else if(Robot.ext.getRawButton(5))      //when ext(5) hook in
        {
            Robot.fricWheel.set(1);
        }
        else if(Robot.ext.getRawButton(3))      //when ext(3) climb (hook and winch in)
        {
            Robot.fricWheel.set(0.825);
            Robot.climbWinch.set(-0.8);
        }
        else if(Robot.logi1.getRawButton(6))    //when logi1(6) winch release
        {
            Robot.climbWinch.set(0.2);  
        }
        else
        {
            Robot.fricWheel.set(0);             //all motor stop
            Robot.climbWinch.set(0);
        }
    }
}
