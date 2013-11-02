package com.example.batmobile.arduino;

import com.example.batmobile.RotationController;

/**
 * Created by IZubkov on 11/2/13.
 */
public class ShieldBotUtility {

    public static ControlSignal CreateControlSignalFromVector(float pitch, float roll){

        float current_max_spin_value;

        if (pitch > RotationController.AVG_PITCH_VALUE){
            current_max_spin_value =  (pitch - RotationController.AVG_PITCH_VALUE) * ShieldBot.MAX_FORWARD_SPIN_VALUE / (RotationController.MAX_PITCH_VALUE - RotationController.AVG_PITCH_VALUE);
        }
        else{
            current_max_spin_value =  (RotationController.AVG_PITCH_VALUE - pitch) * ShieldBot.MAX_REVERS_SPIN_VALUE / (RotationController.AVG_PITCH_VALUE - RotationController.MIN_PITCH_VALUE);
        }

        if(current_max_spin_value > ShieldBot.MAX_FORWARD_SPIN_VALUE)
            current_max_spin_value = ShieldBot.MAX_FORWARD_SPIN_VALUE;

        if(current_max_spin_value < ShieldBot.MAX_REVERS_SPIN_VALUE)
            current_max_spin_value = ShieldBot.MAX_REVERS_SPIN_VALUE;

        float rollratio = Math.abs(roll)/(RotationController.MAX_ROLL_VALUE - RotationController.AVG_ROLL_VALUE);

        float leftRightDelta = Math.round(rollratio * ShieldBot.MAX_FORWARD_SPIN_VALUE);

        int leftSpin, rightSpin;
        leftSpin = rightSpin = Math.round(current_max_spin_value);

        if (roll > 0){ //turn left
            if (current_max_spin_value > 0)
                leftSpin -= leftRightDelta;
            else
                leftSpin += leftRightDelta;
        }else{ //turn right
            if (current_max_spin_value > 0)
                rightSpin -= leftRightDelta;
            else
                rightSpin += leftRightDelta;
        }

        if(leftSpin > ShieldBot.MAX_FORWARD_SPIN_VALUE)
            leftSpin = ShieldBot.MAX_FORWARD_SPIN_VALUE;

        if(leftSpin < ShieldBot.MAX_REVERS_SPIN_VALUE)
            leftSpin = ShieldBot.MAX_REVERS_SPIN_VALUE;

        if(rightSpin > ShieldBot.MAX_FORWARD_SPIN_VALUE)
            rightSpin = ShieldBot.MAX_FORWARD_SPIN_VALUE;

        if(rightSpin < ShieldBot.MAX_REVERS_SPIN_VALUE)
            rightSpin = ShieldBot.MAX_REVERS_SPIN_VALUE;

        ControlSignal signal = new ControlSignal(ShieldBot.COMMAND_DRIVE, (byte)leftSpin, (byte)rightSpin);

        return signal;

    }
}
