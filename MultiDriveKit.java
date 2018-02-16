package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;


/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * In this case that robot is a Pushbot.
 * See PushbotTeleopTank_Iterative and others classes starting with "Pushbot" for usage examples.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 *
 * Motor channel:  Left  drive motor:        "left_drive"
 * Motor channel:  Right drive motor:        "right_drive"
 * Motor channel:  Manipulator drive motor:  "left_arm"
 * Servo channel:  Servo to open left claw:  "left_hand"
 * Servo channel:  Servo to open right claw: "right_hand"
 */

//--------------------------------------------------------------------------------------
/* Allows hardware map to be used in other opmodes*/

public class MultiDriveKit extends OpMode {

    public Gamepad controller1 = gamepad1;
    public Gamepad controller2 = gamepad2;
    //---------------------------------------------------------------------------------------
    /* Activate motors and define them as a variable */
    public DcMotor motor1 = null;
    public DcMotor motor2 = null;
    public DcMotor motor3 = null;
    public DcMotor motor4 = null;
    public DcMotor clawMotor = null;
    public Servo servo1 = null;
    public Servo servo2 = null;
    public Servo jewelServo = null;
    public DcMotor tankMotor1 = null;
    public DcMotor tankMotor2 = null;
    double power = 0.35;
    //-------------------------------------------------------------------------------
    /*Access the hardware on the robot controller*/
    HardwareMap hwMap = null;
    /*Records the time that the opmode is running*/
    private ElapsedTime period = new ElapsedTime();

    //--------------------------------------------------------------------------------
    public void holonomicDrive() {

        //function to swap controllers if dpad_down is true
        if (controller1.dpad_down) {
            controllerSwap();
        }
        //increases speed with y
        if (controller1.y) {
            increaseSpeed();
        }
        //reduces speed with x
        if (controller1.x) {
            reduceSpeed();
        }
        //move commands
        if (controller1.left_stick_y == -1) {
            holoMoveForward(power);
        } else if (controller1.left_stick_y == 1) {
            holoMoveBackward(power);
        } else if (controller1.left_stick_x == 1) {
            holoMoveRight(power);
        } else if (controller1.left_stick_x == -1) {
            holoMoveLeft(power);
        }


    }
    public void tankDrive() {
        if (controller1.dpad_down) {
            controllerSwap();
        }
        //increases speed with y
        if (controller1.y) {
            increaseSpeed();
        }
        //reduces speed with x
        if (controller1.x) {
            reduceSpeed();
        }
        if (controller1.left_stick_y == -1) {
            tankMoveForward(power);
        } else if (controller1.left_stick_y == 1) {
            tankMoveBackward(power);
        }

    }

    //----------------------------------------------------------------------------------------------
    //Tank Drive Functions
    public void tankMoveForward(double power) {
        tankMotor1.setPower(power);
        tankMotor2.setPower(power);
    }
    public void tankMoveBackward(double power) {
        tankMotor1.setPower(-power);
        tankMotor2.setPower(-power);
    }
    public void tankTurnClockwise(double power) {
        tankMotor1.setPower(power);
        tankMotor2.setPower(-power);
    }
    public void tankTurnCounterClockwise(double power) {
        tankMotor1.setPower(-power);
        tankMotor1.setPower(power);
    }
    //Controller Swap Function
    public void controllerSwap() {
        controller1 = controller2;
        controller2 = controller1;
    }

    //Speed Change Functions
    public void reduceSpeed() {
        if (power == 0.5) {
            power = 0.35;
        } else {
            power = 0.25;
        }
    }
    public void increaseSpeed() {
        if (power == 0.25) {
            power = 0.35;
        } else {
            power = 0.5;
        }
    }

    //Holonomic Functions
    public void holoMoveForward(double power) {
        motor1.setPower(power);
        motor2.setPower(-power);
        motor3.setPower(power);
        motor4.setPower(-power);
    }
    public void holoMoveBackward(double power) {
        motor1.setPower(-power);
        motor2.setPower(power);
        motor3.setPower(-power);
        motor4.setPower(power);
    }
    public void holoMoveLeft(double power) {
        motor1.setPower(-power);
        motor2.setPower(-power);
        motor3.setPower(power);
        motor4.setPower(power);
    }
    public void holoMoveRight(double power) {
        motor1.setPower(power);
        motor2.setPower(power);
        motor3.setPower(-power);
        motor4.setPower(-power);
    }
    public void holoTurnClockwise(double power) {
        motor1.setPower(-power);
        motor2.setPower(-power);
        motor3.setPower(-power);
        motor4.setPower(-power);
    }
    public void holoTurnCounterClockwise(double power) {
        motor1.setPower(-power);
        motor2.setPower(-power);
        motor3.setPower(-power);
        motor4.setPower(-power);
    }
    //----------------------------------------------------------------------------------------------
    public void holonomicInit (HardwareMap ahwMap){

        hwMap = ahwMap;
        motor1 = hwMap.dcMotor.get("motor1");
        motor2 = hwMap.dcMotor.get("motor2");
        motor3 = hwMap.dcMotor.get("motor3");
        motor4 = hwMap.dcMotor.get("motor4");

        //Sets the motors to FORWARD direction, FORWARD=Clockwise, REVERSE=CounterClockwise
        motor1.setDirection(DcMotor.Direction.FORWARD); // MAY NEED TO CHANGE LATER (REVERSE)
        motor2.setDirection(DcMotor.Direction.FORWARD);// MAY NEED TO CHANGE LATER (REVERSE)
        motor3.setDirection(DcMotor.Direction.FORWARD);//MAY NEED TO CHANGE LATER (REVERSE)
        motor4.setDirection(DcMotor.Direction.FORWARD);//MAY NEED TO CHANGE LATER (REVERSE)

        /* Set all motors to zero power = no movement */
        motor1.setPower(0);
        motor2.setPower(0);
        motor3.setPower(0);
        motor4.setPower(0);

        // Set all motors to run without encoders.
        motor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor4.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        double power = 0.35;
    }
    public void tankDriveInit (HardwareMap ahwMap) {
        hwMap = ahwMap;

        motor1 = hwMap.dcMotor.get("motor1");
        motor2 = hwMap.dcMotor.get("motor2");


        //Sets the motors to FORWARD direction, FORWARD=Clockwise, REVERSE=CounterClockwise
        motor1.setDirection(DcMotor.Direction.FORWARD); // MAY NEED TO CHANGE LATER (REVERSE)
        motor2.setDirection(DcMotor.Direction.FORWARD);// MAY NEED TO CHANGE LATER (REVERSE)


        /* Set all motors to zero power = no movement */
        motor1.setPower(0);
        motor2.setPower(0);


        // Set all motors to run without encoders.
        motor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        double power = 0.35;

    }

    /***
     *
     * waitForTick implements a periodic delay. However, this acts like a metronome with a regular
     * periodic tick.  This is used to compensate for varying processing times for each cycle.
     * The function looks at the elapsed cycle time, and sleeps for the remaining time interval.
     *
     * @param periodMs  Length of wait cycle in mSec.
     */

    public void waitForTick(long periodMs) {

        long remaining = periodMs - (long) period.milliseconds();

        // sleep for the remaining portion of the regular cycle period.
        if (remaining > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Reset the cycle clock for the next pass.
        period.reset();
    }
    @Override
    public void init() {
        holonomicInit(hwMap);
        //tankDriveInit(hwMap);
    }
    @Override
    public void init_loop() {}
    @Override
    public void start() {}
    @Override
    public void loop() {
        holonomicDrive();
        //tankDrive();
    }
    @Override
    public void stop() {}


}

