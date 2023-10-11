package frc.robot;


import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import frc.robot.autos.*;
import frc.robot.commands.*;
import frc.robot.subsystems.*;



/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */

public class RobotContainer {
    /* Controllers */
    public final XboxController driver = new XboxController(0);
    public final XboxController operator = new XboxController(1);

    /* Drive Controls */
    private final double translationAxis = XboxController.Axis.kLeftY.value;
    private final int strafeAxis = XboxController.Axis.kLeftX.value;
    private final int rotationAxis = XboxController.Axis.kRightX.value;

    /* Driver Buttons */
    private final JoystickButton zeroGyro = new JoystickButton(driver, XboxController.Button.kBack.value);
    private final JoystickButton robotCentric = new JoystickButton(driver, XboxController.Button.kLeftBumper.value);

    /*Operator Buttons */
    // private final JoystickButton opAButton = new JoystickButton(operator, XboxController.Button.kA.value);
    // private final JoystickButton opBButton = new JoystickButton(operator, XboxController.Button.kB.value);

    /* Subsystems */
    public final Swerve s_Swerve = new Swerve();
    public static Wrist wrist = new Wrist();
    public static Intake intake = new Intake();
 


    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        final int translate = (int) (translationAxis);
        final int strafe = (int) (strafeAxis);
        

        s_Swerve.setDefaultCommand(
            new TeleopSwerve(
                s_Swerve, 
                () -> -driver.getRawAxis(translate), 
                () -> -driver.getRawAxis(strafe), 
                () -> -driver.getRawAxis(rotationAxis), 
                () -> robotCentric.getAsBoolean()
            )
        );
        this.wrist.zero();
        this.wrist.setSetpoint(0);
        // Configure the button bindings
        configureButtonBindings();
        //autonomousOptions();
    }

    /**
     * Use this method to define your button->command mappings. Buttons can be created by
     * instantiating a {@link GenericHID} or one of its subclasses ({@link
     * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
     * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */

    private void configureButtonBindings() {
        /* Driver Buttons */
        zeroGyro.onTrue(new InstantCommand(() -> s_Swerve.zeroGyro()));                

        /* Operator Buttons */
        //Robot aligns with apriltags while operator presses A and reflective tape while operator presses B
        // Command aprilTagLineup = new VisionLineup(s_Swerve, candle, 1);
        // opAButton.whileTrue(aprilTagLineup);
        // Command reflectiveTapeLineup = new VisionLineup(s_Swerve, candle, 2);
        // opBButton.whileTrue(reflectiveTapeLineup);

        

    }


    public void teleopPeriodic() {
        //Fix Gyro from Autos
        s_Swerve.setGyroOffset(0.0);

        // Has the pid running the whole time 
        this.wrist.driveTowardsPid();


       

        // Moves the wrist using the pid 
        if (driver.getRightBumperPressed()){
            wrist.setSetpoint(0);
        }
        if (driver.getLeftBumperPressed()){
            wrist.setSetpoint(1);
        }

        // Sets the wrist to the ground to pick up cones
        if (driver.getAButtonPressed()){
            intake.intake_on(.5);
        }
  
        // Stops intake motor
        if (driver.getAButtonReleased()){
            intake.intake_on(0.08);
        }

        if(driver.getBButtonPressed()){
            intake.intake_on(-.9);
        }

        if(driver.getBButtonReleased()){
            intake.intake_on(0);
        }
        

    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */public Command getAutonomousCommand() {
    // Get the selected Auto in smartDashboard
    //return m_chooser.getSelected();
    return new engage(s_Swerve);
}

/**
 * Use this to set Autonomous options for selection in Smart Dashboard
 */
private void autonomousOptions() {
  // Adds Autonomous options to chooser
  //m_chooser.addOption("1", new Auto1(s_Swerve));
  //m_chooser.addOption("2", new Auto2(s_Swerve));

  // Put the chooser on the dashboard
  //SmartDashboard.putData(m_chooser);
}
}
