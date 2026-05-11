package main.java.model.entity.skills;

import main.java.controller.CombatFlow;

public interface SkillWithCondition {
    void refreshCondition(CombatFlow combatFlow);
}
