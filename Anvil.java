package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.lang.Throwable;

/**
 * Created by dcrenshaw on 3/3/18.
 * Extensible class derived from Metal. Used to invoke Metal upon drive trains other than Holonomic
 */

public class Anvil {
    //Define servo and motor variables
    public DcMotor motor1, motor2, motor3, motor4;
    public DcMotor clawMotor;
    public Servo servo1, servo2;
    public Servo jewelServo;
    //Reference to mapped servo/motor controller
    private HardwareMap hwMap;

    public Gamepad controller1, controller2;
    
    public Telemetry telemetry;

    private double prevailingSpeed = 0.35;

    public DcMotor[] forward;
    public DcMotor[] right;
    public DcMotor[] left;
    
    public boolean hs = true;

    public void init (HardwareMap ahwMap, String type) throws Throwable {
        hwMap = ahwMap;

        //Define and connect variables to their matching motors on the robot
        clawMotor = hwMap.dcMotor.get("clawMotor");
        servo1 = hwMap.servo.get("servo1");
        servo2 = hwMap.servo.get("servo2");
        jewelServo = hwMap.servo.get("jewelServo");

        //Sets the motors to appropriate direction, FORWARD=Clockwise, REVERSE=CounterClockwise
        //Define and initialize ALL installed servos. Here is an example snippet:
     /* leftClaw = hwMap.servo.get("left_hand");
        rightClaw = hwMap.servo.get("right_hand");
        leftClaw.setPosition(MID_SERVO);
        rightClaw.setPosition(MID_SERVO);
    */
        switch (type) {
            case "HOLONOMIC":
                //Assign motors
                motor1 = hwMap.dcMotor.get("motor1");
                motor2 = hwMap.dcMotor.get("motor2");
                motor3 = hwMap.dcMotor.get("motor3");
                motor4 = hwMap.dcMotor.get("motor4");
                //Set motor directions
                motor1.setDirection(DcMotor.Direction.FORWARD);
                motor2.setDirection(DcMotor.Direction.REVERSE);
                motor3.setDirection(DcMotor.Direction.FORWARD);
                motor4.setDirection(DcMotor.Direction.REVERSE);
                //Set motor purposes
                forward = new DcMotor[]{motor1, motor2, motor3, motor4};
                right = new DcMotor[]{motor2, motor4};
                left = new DcMotor[]{motor1, motor3};
                hs = false;
                telemetry.addLine("You are using an unoptimized drive system for Holonomic. Please use Metal.");
                break;
            case "TANK":
                motor1 = hwMap.dcMotor.get("motor1");
                motor2 = hwMap.dcMotor.get("motor2");
                motor1.setDirection(DcMotor.Direction.FORWARD);
                motor2.setDirection(DcMotor.Direction.FORWARD);
                forward = new DcMotor[]{motor1, motor2};
                right = new DcMotor[]{motor2};
                left = new DcMotor[]{motor1};
                break;
            default:
                throw new Throwable("Invalid type passed to Anvil's init function.");
        }
        for (DcMotor x : forward) {x.setPower(0); x.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);}
    }
    //Movement and turning methods compatible with all drive trains
    public void moveForward() {
        for (DcMotor x : forward) x.setPower(prevailingSpeed);
    }
    public void turnRight() {
        if (hs) {
            for (DcMotor x : right) x.setPower(-prevailingSpeed / 2);
            for (DcMotor x : left) x.setPower(prevailingSpeed / 2);
        }
        else {
            for (DcMotor x : right) x.setPower(-prevailingSpeed);
            for (DcMotor x : left) x.setPower(prevailingSpeed);
        }
    }
    public void turnLeft() {
        if (hs) {
            for (DcMotor x : left) x.setPower(-prevailingSpeed / 2);
            for (DcMotor x : right) x.setPower(prevailingSpeed / 2);
        }
        else {
            for (DcMotor x : left) x.setPower(-prevailingSpeed);
            for (DcMotor x : right) x.setPower(prevailingSpeed);
        }
    }
    public void moveBackward() {
        for (DcMotor x : forward) x.setPower(-prevailingSpeed);
    }
    //Holonomic specific movements
    public void holoMoveRight() {
        motor1.setPower(prevailingSpeed);
        motor2.setPower(-prevailingSpeed);
        motor3.setPower(prevailingSpeed);
        motor4.setPower(-prevailingSpeed);
    }
    public void holoMoveLeft() {
        motor1.setPower(-prevailingSpeed);
        motor2.setPower(prevailingSpeed);
        motor3.setPower(-prevailingSpeed);
        motor4.setPower(prevailingSpeed);
    }
}
