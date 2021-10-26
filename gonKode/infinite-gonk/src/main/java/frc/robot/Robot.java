package frc.robot;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
  
  public static Joystick logi1 = new Joystick(0); //0 drives forward, backward, and crab
  public static Joystick logi = new Joystick(1);  //1 does twisting
  public static Joystick ext = new Joystick(2);   //2 Controls all button for shooting and axis for angling the pitcher

  public static CANSparkMax frontL = new CANSparkMax(1, MotorType.kBrushless);  //assigns motor ports and types
  public static CANSparkMax frontR = new CANSparkMax(2, MotorType.kBrushless);
  public static CANSparkMax backL = new CANSparkMax(12, MotorType.kBrushless);
  public static CANSparkMax backR = new CANSparkMax(11, MotorType.kBrushless);
  public static CANSparkMax pitcher = new CANSparkMax(10, MotorType.kBrushless);
  public static CANSparkMax belt = new CANSparkMax(5, MotorType.kBrushless);
  public static CANSparkMax topLaunch = new CANSparkMax(6, MotorType.kBrushless);
  public static CANSparkMax bottomLaunch = new CANSparkMax(7, MotorType.kBrushless);
  public static CANSparkMax intake = new CANSparkMax(9, MotorType.kBrushless);
  public static CANSparkMax pitcherIn = new CANSparkMax(4, MotorType.kBrushed);
  public static CANSparkMax climbWinch = new CANSparkMax(3, MotorType.kBrushless);
  public static CANSparkMax fricWheel = new CANSparkMax(8, MotorType.kBrushed);

  public static CANEncoder flEnc = new CANEncoder(frontL);                       //assigns encoders to motors
  public static CANEncoder frEnc = new CANEncoder(frontR);
  public static CANEncoder blEnc = new CANEncoder(backL);
  public static CANEncoder brEnc = new CANEncoder(backR);
  public static CANEncoder pitchEnc = new CANEncoder(pitcher);
  public static CANEncoder beltEnc = new CANEncoder(belt);
  public static CANEncoder topLaunchEnc = new CANEncoder(topLaunch);
  public static CANEncoder botLaunchEnc = new CANEncoder(bottomLaunch);
  public static CANEncoder climbEnc = new CANEncoder(climbWinch);


  public static CANPIDController frPID = new CANPIDController(frontR);          //PID loops
  public static CANPIDController flPID = new CANPIDController(frontL);
  public static CANPIDController brPID = new CANPIDController(backR);
  public static CANPIDController blPID = new CANPIDController(backL);
  public static CANPIDController pitcherPID = new CANPIDController(pitcher);
  public static CANPIDController uSpeedControl = new CANPIDController(bottomLaunch);
  public static CANPIDController lSpeedControl = new CANPIDController(topLaunch);
  public static CANPIDController beltPID = new CANPIDController(belt);

  public static SendableChooser<Integer> autoChoice = new SendableChooser<>();  //sets the name of box to autoChoice in shuffleboard
  public static PigeonIMU pigeon = new PigeonIMU(42); //used for the gyro in autoBlock - unknown
  public static DigitalInput inSensor = new DigitalInput(0);
  public static DigitalInput outSensor = new DigitalInput(1);
  public static MecanumDrive scoot = new MecanumDrive(frontL, backL, frontR, backR);
  public static DigitalInput zero = new DigitalInput(2);

  public static double kP = 1e-4;
  public static double kI = 3e-7;
  public static byte kD = 0; 
  public static byte kIz = 0; 
  public static byte kFF = 0; 
  public static byte kMaxOutput = 1; 
  public static byte kMinOutput = -1;
  public static short maxRPM = 5700;

  public static double driveP = 1e-3; 
  public static double driveI = 3e-4;
  public static byte driveD = 0; 
  public static byte driveIz = 0; 
  public static byte driveFF = 0; 
  public static byte driveMaxOutput = 1; 
  public static byte driveMinOutput = -1;
  public static short diveRPM = 5700;

  public static double launchCountdown;   //time when auto is initiated or when ext(1)pressed + launchWait
  public static short launchWait = 3000;  //amount in miliseconds to wait before shooting balls - only effects 1/2 launch options
  public static double currentPos;        //current position of the belt ENC
  public static double countDelay = 0;    //sets delay to 0 miliseconds - used in ballCounter
  public static double [] gyroRead = new double[3];
  public static double yawStart;
  public static double startPos;          //when using straight function in auto, becomes frEnc position
  public static double targetAngle;       //angle of the barrel used for shooting
  public static double autoDelay;

  public static boolean ballWasBack = false;
  public static boolean launchStatus = true;  //always true - does not get changed
  public static boolean beltCheck = true;     //when ext(1) or ext(2) released: true - when no balls sensed going out: false - see method launch
  public static boolean ballWasFront = false;
  public static boolean stageStart = true;    //after starting straight becomes false to continue code

  public static int ballCount;
  public static int primeAuto = 1;      //autoBlocks option 1
  public static int universalAuto = 2;  //autoBlocks option 2
  public static int straightBack = 3;   //autoBlocks option 3
  public static int ballsLeft;
  public static int lastCount = 0;      //tells computer there are 0 balls - used in ballCounter
  int controlMultiply = 1;

  ballCounter ballsOut = new ballCounter();
  autoBlocks basicallyAI = new autoBlocks();
  limelight vision = new limelight();
  climb climber = new climb();
  launchAngler sniper = new launchAngler();
  //public static int toggleLime = 1;
  // colorParse colorWheel = new colorParse();

  public static void launch(int mode)  {
    uSpeedControl.setP(kP);
    uSpeedControl.setI(kI);
    uSpeedControl.setD(kD);
    uSpeedControl.setIZone(kIz);
    uSpeedControl.setFF(kFF);
    uSpeedControl.setOutputRange(kMinOutput, kMaxOutput);

    lSpeedControl.setP(kP);
    lSpeedControl.setI(kI);
    lSpeedControl.setD(kD);
    lSpeedControl.setIZone(kIz);
    lSpeedControl.setFF(kFF);
    lSpeedControl.setOutputRange(kMinOutput, kMaxOutput);

    System.out.println("Welcome User");
    System.out.println("Drive Safe!");
  
    if(ballCount > ballsLeft && beltCheck == false) //will work when interal ball count > ballsLeft and beltCheck false 
    {
      uSpeedControl.setReference(2250, ControlType.kVelocity); //was 3000rpm controls top launch overall speed
      lSpeedControl.setReference(2500, ControlType.kVelocity); //controls bottom launch overall speed

      if(System.currentTimeMillis() >= launchCountdown && launchStatus == true && mode == 2)  //does not wait before beginning launch
        {
          if(outSensor.get() )            //when running launch, if the sensor senses a ball going out, it reverses the internal belt
          {
            belt.set(-0.15);
          }
          else if (outSensor.get() == false)
          {
           belt.set(0.35);
          }

        }
      else if(System.currentTimeMillis() >= launchCountdown &&  launchStatus == true && ballCount > ballsLeft && mode == 1) //current mode, waits for a moment before launch
      { //see launchCountdown, 
        // if(outSensor.get() )              //when running launch, if the sensor senses a ball going out, it reverses the internal belt
        // {
        //  belt.set(-0.1);
        // }
        // else 
        if (outSensor.get() == false)   //if no ball is leaving belt goes forward
        {
          belt.set(0.35);
        }
      }
    }
    else if(outSensor.get())             //if it senses a ball leaving the belt will run backwards - creates space between balls
    {             
      belt.set(-0.2);                   
    }
    else if(outSensor.get() == false)    //checks that nothing is going out and stops the belt if there is nothing
    {
      belt.set(0);
      beltCheck = false;                 //see beltCheck
    }
    else
    {
      topLaunch.set(0);                  //stop all
      bottomLaunch.set(0);
      belt.set(0);
    }
  }



  public static void beltIndexer() {
    if(inSensor.get())
    {                  
      if(ballCount < 4)
      {
        currentPos = beltEnc.getPosition() + 0.1; //when loading balls it moves the belt forward slightly to prepare for the next ball
      }
      else
      {
        currentPos = beltEnc.getPosition() + 0.0001;
      }
    }
  }
 
  @Override
  public void robotInit() {
    autoChoice.addOption("6 Ball", primeAuto);
    autoChoice.addOption("Universal Auto", universalAuto);
    autoChoice.addOption("Straight Back", straightBack);
    Shuffleboard.getTab("Preround").add("Auto Choice", autoChoice).withWidget(BuiltInWidgets.kComboBoxChooser);

    pitcher.setInverted(true);
    topLaunch.setInverted(false);
    bottomLaunch.setInverted(true);
    belt.setInverted(false);
    pitcherIn.setInverted(false);
    currentPos = beltEnc.getPosition();
    frEnc.setPosition(0);
    flEnc.setPosition(0);
    blEnc.setPosition(0);
    brEnc.setPosition(0);
    
    frPID.setP(kP);
    frPID.setI(kI);
    frPID.setD(kD);
    frPID.setIZone(kIz);
    frPID.setFF(kFF);
    frPID.setOutputRange(kMinOutput, kMaxOutput);

    brPID.setP(kP);
    brPID.setI(kI);
    brPID.setD(kD);
    brPID.setIZone(kIz);
    brPID.setFF(kFF);
    brPID.setOutputRange(kMinOutput, kMaxOutput);

    flPID.setP(kP);
    flPID.setI(kI);
    flPID.setD(kD);
    flPID.setIZone(kIz);
    flPID.setFF(kFF);
    flPID.setOutputRange(kMinOutput, kMaxOutput);

    blPID.setP(kP);
    blPID.setI(kI);
    blPID.setD(kD);
    blPID.setIZone(kIz);
    blPID.setFF(kFF);
    blPID.setOutputRange(kMinOutput, kMaxOutput);

    pitcher.setOpenLoopRampRate(0.5);
    pitcher.setClosedLoopRampRate(0.5);
  }

  
  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {
    autoDelay = System.currentTimeMillis();     //set equal to time when auto is initiated - look to autoBlocks
    launchCountdown = System.currentTimeMillis() + launchWait;  //time when auto is initiated + launchWait - look to launch method 
    ballCount = 3;  //when auto initiated knows that there are 3 balls
  }

  
  @Override
  public void autonomousPeriodic() {
    vision.pipeline.setDouble(0);
    ballsOut.ballsIn();
    beltIndexer();
    int finalChoice = universalAuto;  //only runs universalAuto
    //int finalChoice = (int)autoChoice.getSelected(); //#goals
    basicallyAI.fullAuto(finalChoice);
  }


  
  @Override
  public void teleopPeriodic() {
    SmartDashboard.putNumber("Angle", pitchEnc.getPosition()*(-360/71) + 50);
    SmartDashboard.putNumber("Target Angle", targetAngle);
    SmartDashboard.putNumber("Range", vision.rangeFinder());
    ballsOut.ballsIn();
    beltIndexer();
    pitcherPID.setP(1e-5);
    pitcherPID.setI(1e-7);

    if(outSensor.get() == true)
    {
      ballsLeft = ballCount - 1;
    }
    
    if(zero.get())
    {
      pitchEnc.setPosition(0);
    }

    if(ext.getRawButton(2)) 
    {
      if(ext.getRawButton(6))
      {
        belt.set(0.3);
      }
      else if(ext.getRawButton(7))
      {
        belt.set(-0.3);
      }
      else
      {
        belt.set(0);
      }

      if(ext.getRawButton(1))
      {
        topLaunch.set(0.4);             //og 0.4,revision 1:0.3950
        bottomLaunch.set(0.6);         //og 0.7,revision 1:0.4386
      }
      else
      {
        topLaunch.set(0);
        bottomLaunch.set(0);
      }   
    }
    else if(ext.getRawButtonPressed(1) )
    {
      launchCountdown = System.currentTimeMillis() + launchWait;
      ballsLeft = 0;
    }
    else if(ext.getRawButton(1) )
    {
        launch(1);
    }
    else
    {
      if(beltEnc.getPosition() < currentPos)
      {
        belt.set(0.5);
      }
      else if(beltEnc.getPosition() > currentPos && inSensor.get() == false)
      {
        belt.set((currentPos-beltEnc.getPosition())/10);
      }
      else
      {
        belt.set(0);
      }

      topLaunch.set(0);
      bottomLaunch.set(0);
    }

    if(ext.getRawButton(8))           //runs intake and pitcher intake forwards (collect)
    {
      pitcherIn.set(1);
      intake.set(0.3);
    }
    else if(ext.getRawButton(9))      //runs intake and pitcher intake backwards (poop)
    {
      pitcherIn.set(-1);
      intake.set(-0.3);
    }
    else
    {
      pitcherIn.set(0);
      intake.set(0);
    }

    if(logi1.getRawButtonReleased(1))
    {
      controlMultiply = controlMultiply * (-1); //flips translational controls (forward/backward, left/right) 
    }    
      
    if(ext.getRawButtonReleased(1) || ext.getRawButtonReleased(2))
    {
      beltCheck = true;                
    }

    if(logi.getRawButton(1))
    {                                   //while holding logi 1 - limelight on, limelight angling, limelight positioning
      vision.pipeline.setDouble(0);     //when (1) limelight is off
      vision.camControl();              //Made the robot spin to find target
      sniper.tip();                     //angles pitcher based on limelight data
    }
    else
    {
      vision.pipeline.setDouble(1);     //don't get blinded
      scoot.driveCartesian(logi1.getRawAxis(0) * controlMultiply, -logi1.getRawAxis(1) * controlMultiply, logi.getRawAxis (0)); //normal controls for drive base
      
      if(ext.getRawButton(8))           //while holding ext 8 the pitcher will rotate to its specific position to load balls
      {          
        if(pitchEnc.getPosition() > 1)
        {
          pitcher.set(-0.25);
        }
        else if(pitchEnc.getPosition() < -1.1)
        {
          pitcher.set(0.25);
        }
        else
        {
          pitcher.set((-0.24 * pitchEnc.getPosition()));
        }
      }
      else if(ext.getRawButton(10))
      {
        sniper.angleSet(0);
      }
      else if(ext.getRawAxis(0) < 0.05 && ext.getRawAxis(0) > -0.05)
      {
        pitcherPID.setReference(0, ControlType.kVelocity);
      }
      else
      {
        if(ext.getRawButton(2))
        {
          pitcher.set(ext.getRawAxis(0)/6); //was *0.25
        }
      }

      if(ext.getRawButton(4) || ext.getRawButton(5) || ext.getRawButton(3))
      {
        climber.winch();
      }
      else
      {
        fricWheel.set(0);
        climbWinch.set(0);
      }    
    }  
  }
 
  @Override
  public void testPeriodic() {}

}
