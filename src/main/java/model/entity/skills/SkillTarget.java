package main.java.model.entity.skills;

import java.util.*;

public class SkillTarget {

    private Map<Integer, TargetState> targets = new LinkedHashMap<>();

    public void addTargetAndDecision(
            String target,
            String decision,
            int target_index,
            int input_index,
            int target_number
    ){
        TargetState state = targets.getOrDefault(target_index, new TargetState());

        // update target
        state.target.put(target_number, target);

        // update decision
        Map<Integer, String> inputMap =
                state.decision.getOrDefault(target, new LinkedHashMap<>());

        inputMap.put(input_index, decision);
        state.decision.put(target, inputMap);

        targets.put(target_index, state);
    }

    public void addTarget(String target, int target_index, int target_number) {
        targets.computeIfAbsent(target_index, k -> new TargetState())
                .target.put(target_number, target);
    }

    public Collection<String> getTarget(int target_index) {
        if (targets.get(target_index) == null) {
            return new ArrayList<>();
        }
        return targets.get(target_index).target.values();
    }

    public String getDecision(String target, int target_index, int input_index) {
        if (targets.get(target_index) == null) {
            return "";
        }
        if (targets.get(target_index).decision.get(target) == null) {
            return "";
        }
        return targets.get(target_index).decision.get(target).get(input_index);
    }


    public class TargetState {
        Map<Integer, String> target = new LinkedHashMap<>();
        Map<String, Map<Integer, String>> decision = new LinkedHashMap<>();
    }

}
