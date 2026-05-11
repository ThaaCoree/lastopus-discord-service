package main.controller.event.events;

import model.entity.skills.SkillInstance;
import model.entity.units.Unit;
import model.type.DamageType;
import model.type.SkillType;

import java.util.Collection;
import java.util.List;

public class SkillUse {
    public Unit source;
    public Collection<SkillType> type;
    public double health_cost;
    public double mana_cost;
    public SkillInstance skill_instance;

    public SkillUse(Unit source, SkillInstance skill_instance, Collection<SkillType> type, double health_cost, double mana_cost) {
        this.source = source;
        this.health_cost = health_cost;
        this.mana_cost = mana_cost;
        this.type = type;
        this.skill_instance = skill_instance;
    }
}
