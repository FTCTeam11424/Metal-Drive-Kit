package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by dcrenshaw on 3/4/18.
 * Clasp is an intelligent teleop designed to interface with Anvil and provide an overhaul to
 * classic teleop. Clasp is built for the Tank drive train, but should work with any drive train.
 */
/* CHANGELOG:
 * Anvil-controlled speed is no longer used anywhere in Clasp. A new, more flexible speed control
   system has been added in its place: Computer-Assisted Zones
 * Added changelog
 */

public class ClaspTeleop extends OpMode {
   
   //This must be modified before it is ready for use. it makes broken assumptions.
   
    public Anvil metal;

    public boolean speedMode = true;

    public double[] zoneList = new double[]{0.35, 0.5, 0.75};

    public void init() {
        metal = new Anvil();
        metal.init(hardwareMap, "TANK", telemetry);
    }
    public void loop() {
        //Handle buttons first
        if (gamepad1.a) { //Emergency brake. Stops all motors immediately.
            metal.rest();
            while (gamepad1.a) {
                if (!(gamepad1.a)) break;
            }
        }
        else if (gamepad1.b) {
            //Unused button
        }
        else if (gamepad1.x) {
            //Unused button
        }
        else if (gamepad1.y) {
            //Unused button
        }
        else if (gamepad1.dpad_left) speedMode ^= true;
        else if (gamepad1.dpad_down) swapControllers();
        //Handle controls
        if (gamepad1.atRest()) {
            metal.rest();
        }
        else {
            if (Math.abs(gamepad1.left_stick_x) > Math.abs(gamepad1.left_stick_y)) {
                if (gamepad1.left_stick_x > 0) {
                    if (speedMode) {
                        metal.turnLeft(computerAssistedZones());
                    } else {
                        metal.turnLeft(gamepad1.left_stick_x);
                    }
                }
                else {
                    if (speedMode) {
                        metal.turnRight(computerAssistedZones());
                    } else {
                        metal.turnRight(-gamepad1.left_stick_x);
                    }
                }
            }
            else {
                if (gamepad1.left_stick_y > 0) {
                    if (speedMode) {
                        metal.moveBackward(computerAssistedZones());
                    } else {
                        metal.moveBackward(gamepad1.left_stick_y);
                    }
                }
                else {
                    if (speedMode) {
                        metal.moveForward(computerAssistedZones());
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
    public double computerAssistedZones() {
        double a = Math.sqrt(Math.pow(gamepad1.left_stick_x, 2) + Math.pow(gamepad1.left_stick_y, 2));
        return zoneList[(int) Math.ceil(a*zoneList.length) - 1];
    }
}
