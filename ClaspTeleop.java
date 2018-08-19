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

public class ClaspTeleop extends OpMode implements Gamepad.GamepadCallback {
    public Anvil metal; //hardware drive class
    public NXTeleManager telemanager; //fancified telemetry manager
    public String name = "Clasp";

    /*
     * Clasp implements two main speed determinant systems:
     * CAZ, or Computer Assisted Zones, which segments the joystick range into distance-based speed
     * zones. Currently, there are three distinct speeds in the speed list, and so the joystick
     * range is split into even thirds and the robot's speed is based on the joystick's distance
     * from its origin.
     * The second system is reflective, and simply moves the robot at a speed equal to the
     * distance of the joystick from its origin.
     */

    public boolean speedMode = true;

    public double[] zoneList = new double[]{0.35, 0.5, 0.75};

    public void init() {
        try {
            metal = new Anvil(hardwareMap, "TANK", telemanager);
            telemanager = new NXTeleManager(telemetry);
            gamepad1 = new Gamepad(this); //fix gamepad1 to work with Clasp's callback
            telemanager.create("Speed-mode: ", "CAZ");
        }
        catch (Throwable e) {

        }
    }

    public void loop() {
        //Handle controls
        if (gamepad1.atRest()) {
            metal.rest(); //make sure controller stasis gets enforced
        } else {
            if (Math.abs(gamepad1.left_stick_x) > Math.abs(gamepad1.left_stick_y)) { //most significant difference wins
                if (gamepad1.left_stick_x > 0) {
                    if (speedMode) {
                        metal.turnLeft(computerAssistedZones());
                    } else {
                        metal.turnLeft(gamepad1.left_stick_x);
                    }
                } else {
                    if (speedMode) {
                        metal.turnRight(computerAssistedZones());
                    } else {
                        metal.turnRight(-gamepad1.left_stick_x);
                    }
                }
            } else {
                if (gamepad1.left_stick_y > 0) {
                    if (speedMode) {
                        metal.moveBackward(computerAssistedZones());
                    } else {
                        metal.moveBackward(gamepad1.left_stick_y);
                    }
                } else {
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
        //This may just freeze both controllers, depending on whether Java passes addresses or values here
        Gamepad r = gamepad1; //Runner
        gamepad1 = gamepad2;
        gamepad2 = r;
    }

    public double computerAssistedZones() {
        double a = Math.sqrt(Math.pow(gamepad1.left_stick_x, 2) + Math.pow(gamepad1.left_stick_y, 2));
        return zoneList[(int) Math.floor(a * zoneList.length)];
    }
    public void gamepadChanged(Gamepad g) {
        // May still require work. Don't mash buttons and things will (probably) be fine
        if (gamepad1.a) { //Emergency brake. Stops all motors immediately.
            metal.rest();
            while (gamepad1.a) {
                if (!(gamepad1.a)) break;
            }
        } else if (gamepad1.b) {
            //Unused button
        } else if (gamepad1.x) {
            //Unused button
        } else if (gamepad1.y) {
            //Unused button
        } else if (gamepad1.dpad_left) {
            speedMode ^= true;
            telemanager.update("Speed-mode: ", speedMode ? "CAZ" : "Reflective");
        }
        else if (gamepad1.dpad_down) swapControllers();
    }
}
