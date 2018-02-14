package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * Created by dcrenshaw on 1/27/18.
 *
 * The Metal Drive Kit is a framework for connecting directly to the robot hardware for driving.
 * Metal is intended to be a more feature-rich replacement for existing drive proxy functions.
 */
/* Bug guide
 * Errors:
 * "Null" object has no member...
 * You have attempted to use an uninitialized feature. Initialize it properly.
 */

public class MetalDriveKit {
    //Define servo and motor variables and set them to null
    public DcMotor motor1 = null;
    public DcMotor motor2 = null;
    public DcMotor motor3 = null;
    public DcMotor motor4 = null;
    public DcMotor clawMotor = null;
    public Servo servo1 = null;
    public Servo servo2 = null;
    public Servo jewelServo = null;
    //Reference to mapped servo/motor controller
    private HardwareMap hwMap = null;

    Gamepad controller1 = null;
    Gamepad controller2 = null;

    Thread controllerServer = null;

    private double prevailingSpeed = 0.35;

    public HashMap<String, Runnable> intents = new HashMap<>();

    private ElapsedTime period = new ElapsedTime();

    VuforiaLocalizer vuforia;

    public void init (HardwareMap ahwMap) {
        hwMap = ahwMap;

        //Define and connect variables to their matching motors on the robot
        motor1 = hwMap.dcMotor.get("motor1");
        motor2 = hwMap.dcMotor.get("motor2");
        motor3 = hwMap.dcMotor.get("motor3");
        motor4 = hwMap.dcMotor.get("motor4");
        clawMotor = hwMap.dcMotor.get("clawMotor");
        servo1 = hwMap.servo.get("servo1");
        servo2 = hwMap.servo.get("servo2");
        jewelServo = hwMap.servo.get("jewelServo");

        //Sets the motors to appropriate direction, FORWARD=Clockwise, REVERSE=CounterClockwise
        motor1.setDirection(DcMotor.Direction.FORWARD);
        motor2.setDirection(DcMotor.Direction.REVERSE);
        motor3.setDirection(DcMotor.Direction.FORWARD);
        motor4.setDirection(DcMotor.Direction.REVERSE);
        //Rest all motors
        motor1.setPower(0);
        motor2.setPower(0);
        motor3.setPower(0);
        motor4.setPower(0);
        //Set all motors to run without encoders
        motor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor4.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //Define and initialize ALL installed servos. Here is an example snippet:
     /* leftClaw = hwMap.servo.get("left_hand");
        rightClaw = hwMap.servo.get("right_hand");
        leftClaw.setPosition(MID_SERVO);
        rightClaw.setPosition(MID_SERVO);
    */
    }
    public void setControllers(Gamepad ct1, Gamepad ct2) {
        //This MUST be called to handle control swaps.
        //If this is called, control swaps WILL be handled.
        //Control swaps will be implemented later
        controller1 = ct1;
        controller2 = ct2;
    }
    public void setMotors(double npower) {
        motor1.setPower(npower);
        motor2.setPower(npower);
        motor3.setPower(npower);
        motor4.setPower(npower);
    }
    public void setMapCtlr1() {
        intents.put("ctlr_1_x", new Runnable() {
            @Override
            public void run() {
                prevailingSpeed = 1;
            }
        });
        intents.put("ctlr_1_y", new Runnable() {
            @Override
            public void run() {
                prevailingSpeed = 0.5;
            }
        });
        intents.put("ctlr_1_b", new Runnable() {
            @Override
            public void run() {
                prevailingSpeed = 0.35;
            }
        });
        intents.put("ctlr_1_a", new Runnable() {
            @Override
            public void run() {
                prevailingSpeed = 0.25;
            }
        });
        intents.put("rbt_mov_right", new Runnable() {
            @Override
            public void run() {
                moveRight();
            }
        });
        intents.put("rbt_rest", new Runnable() {
            @Override
            public void run() {
                rest();
            }
        });
        intents.put("rbt_mov_left", new Runnable() {
            @Override
            public void run() {
                moveLeft();
            }
        });
        intents.put("rbt_mov_forward", new Runnable() {
            @Override
            public void run() {
                moveForward();
            }
        });
        intents.put("rbt_mov_backward", new Runnable() {
            @Override
            public void run() {
                moveBackward();
            }
        });
        intents.put("rbt_trn_right", new Runnable() {
            @Override
            public void run() {
                turnRight();
            }
        });
        intents.put("rbt_trn_left", new Runnable() {
            @Override
            public void run() {
                turnLeft();
            }
        });
        intents.put("serve_controller_movement", new Runnable() { //Handles controller input for robot movement
            @Override
            public void run() {
                controllerServer = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        intrpLoop();
                    }
                });
                controllerServer.start();
            }
        });
        intents.put("stop_serving", new Runnable() {
            @Override
            public void run() {
                if (controllerServer != null) {
                    controllerServer.interrupt();
                }
            }
        });
    }
    public void waitForTick(long periodMs) {
        long remaining = periodMs - (long) period.milliseconds();
        //Sleep for the remaining portion of the regular cycle period
        if (remaining > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        //Reset the cycle clock for the next pass
        period.reset();
    }
    public void moveForward() {
        setMotors(prevailingSpeed);
    }
    public void rest() {
        setMotors(0);
    }
    public void moveBackward() {
        setMotors(-prevailingSpeed);
    }
    public void moveLeft() {
        motor1.setPower(-prevailingSpeed);
        motor2.setPower(prevailingSpeed);
        motor3.setPower(-prevailingSpeed);
        motor4.setPower(prevailingSpeed);
    }
    public void moveRight() {
        motor1.setPower(prevailingSpeed);
        motor2.setPower(-prevailingSpeed);
        motor3.setPower(prevailingSpeed);
        motor4.setPower(-prevailingSpeed);
    }
    public void turnLeft() { //counterclockwise
        motor1.setPower(-prevailingSpeed);
        motor2.setPower(prevailingSpeed);
        motor3.setPower(prevailingSpeed);
        motor4.setPower(-prevailingSpeed);
    }
    public void turnRight() { //clockwise
        motor1.setPower(prevailingSpeed);
        motor2.setPower(-prevailingSpeed);
        motor3.setPower(-prevailingSpeed);
        motor4.setPower(prevailingSpeed);
    }
    public void reduceSpeed() {
        if (prevailingSpeed == 0.5) {
            prevailingSpeed = 0.35;
        } else {
            prevailingSpeed = 0.25;
        }
    }
    public void increaseSpeed() {
        if (prevailingSpeed == 0.25) {
            prevailingSpeed = 0.35;
        } else {
            prevailingSpeed = 0.5;
        }
    }
    public void sendMessage(String msg) {
        Runnable intent = intents.get(msg);
        intent.run();
    }
    public void toTop() {
        //Requires testing of the robot to implement.
        //Needs to know how far the claw moves in 100 iterations
    }
    public String getVuMark() {
        int cameraMonitorViewId = hwMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hwMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "AfdO5vj/////AAAAGSpM5tOflkvMvW4RzPkR14sF7ZtBXS06d04V0BL1s3kqEDkbvcN9uoHhoUg+hPC5pKqRAuhHfpPvv6sNrQgXO6gJaL3kzjIOlcOhx35mONJDaQ4lu3cYAxeNISUTaUkmlTajAcqhGeCLj+m+0lNjg2lF3UmfzocsFnwl8Oi6117s9MDLo3/HFTmYw/QLVnSsvdUW6GRg7jnDG1sJJmTXtOkgmbHAGrvqUSevnxjnEw9w2ME69SsbZof7/J3Xyl38xE1ekM8qn3/nC4CsQF5xJFJkbnI4h9aATJx5szNP1Zu1CSON4+WSzynZrd7H4zcVA3rQZvqEuMsQ5OlKsOlsIWdLctOLXSHTcXh7+1iXU+DS";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTrackables.activate();

        //WARNING: This WILL block until it finds a valid instance of the pictogram.
        //Run it concurrently or implement a call to turn it while it looks.
        while (true) {
            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
            if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
                return vuMark.name();
            }
        }
    }
    public void intrpLoop() {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        if (controller1.left_stick_y == 1) {
            moveForward();
        }
        else if (controller1.left_stick_y == -1) {
            moveBackward();
        }
        else if (controller1.left_stick_x == 1) {
            moveRight();
        }
        else if (controller1.left_stick_x == -1) {
            moveLeft();
        }
        else if (controller2.left_stick_x == 1) {
            turnRight();
        }
        else if (controller2.left_stick_x == -1) {
            turnLeft();
        }
        else {
            rest();
        }
    }
}
