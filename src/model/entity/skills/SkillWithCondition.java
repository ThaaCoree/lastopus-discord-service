package model.entity.skills;

import main.controller.CombatFlow;

public interface SkillWithCondition {
    void refreshCondition(CombatFlow combatFlow);
}
