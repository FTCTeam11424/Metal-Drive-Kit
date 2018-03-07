package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by dcrenshaw on 3/4/18.
 * Clasp is an intelligent teleop designed to interface with Anvil and provide an overhaul to
 * classic teleop. Clasp is built for the Tank drive train, but should work with any drive train.
 */

public class ClaspTeleop extends OpMode {
    public Anvil metal;

    public boolean speedMode = true;

    public void init() {
        metal = new Anvil();
        metal.init(hardwareMap, "TANK", telemetry);
    }
    public void loop() {
        //Handle buttons first
        if (gamepad1.a) {
            //Emergency brake. Stops all motors immediately.
            metal.rest();
        }
        else if (gamepad1.b) {
            metal.reduceSpeed();
            //Trap loop
            while (gamepad1.b) {
                if (!(gamepad1.b)) break;
            }
        }
        else if (gamepad1.y) {
            metal.increaseSpeed();
            while (gamepad1.y) {
                if (!(gamepad1.y)) break;
            }
        }
        else if (gamepad1.x) {
            //Open button. Assign game-specific actions here
        }
        else if (gamepad1.dpad_left) {
            speedMode ^= true;
        }
        //Handle controls
        if (gamepad1.atRest()) {
            metal.rest();
        }
        else {
            if (Math.abs(gamepad1.left_stick_x) > Math.abs(gamepad1.left_stick_y)) {
                if (gamepad1.left_stick_x > 0) {
                    if (speedMode) {
                        metal.turnLeft();
                    } else {
                        metal.turnLeft(gamepad1.left_stick_x);
                    }
                }
                else {
                    if (speedMode) {
                        metal.turnRight();
                    } else {
                        metal.turnRight(-gamepad1.left_stick_x);
                    }
                }
            }
            else {
                if (gamepad1.left_stick_y > 0) {
                    if (speedMode) {
                        metal.moveBackward();
                    } else {
                        metal.moveBackward(gamepad1.left_stick_y);
                    }
                }
                else {
                    if (speedMode) {
                        metal.moveForward();
                    } else {
                        metal.moveForward(-gamepad1.left_stick_y);
                    }
                }
            }
        }
    }
    public void swapControllers() {
        Gamepad r = gamepad1; //Runner
        gamepad1 = gamepad2;
        gamepad2 = r;
    }
}
