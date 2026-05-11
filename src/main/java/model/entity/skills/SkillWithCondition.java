package model.entity.skills;

import controller.CombatFlow;

public interface SkillWithCondition {
    void refreshCondition(CombatFlow combatFlow);
}
