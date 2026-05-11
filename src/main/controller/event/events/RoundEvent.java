package main.controller.event.events;

import model.entity.ConditionInstance;
import model.entity.units.Unit;
import model.type.ActType;

import java.util.ArrayList;
import java.util.List;

public class RoundEvent {
    public int current_round;
    public int round_passing;
    public int next_round;

    public RoundEvent(int current_round, int round_passing) {
        this.current_round = current_round;
        this.round_passing = round_passing;
        next_round = current_round+round_passing;
    }

    public int getCurrent_round() {
        return current_round;
    }

    public int getRound_passing() {
        return round_passing;
    }

    public int getNext_round() {
        return next_round;
    }
}
