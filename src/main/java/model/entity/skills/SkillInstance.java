package model.entity.skills;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.entity.ModifierBundle;

public class SkillInstance {
    @JsonIgnore
    private Skill skillData;
    private ModifierBundle instanceBundle = new ModifierBundle();
    private int onCooldown;
    private boolean reserving;

    public SkillInstance(Skill skillData) {
        this.skillData = skillData;
        onCooldown = 0;
        reserving = false;
    }

    public SkillInstance() {

    }

    public void applyModifier() {
        if (reserving) {
            setInstanceBundle(skillData.getSkillModifier());
        } else {
            setInstanceBundle(new ModifierBundle());
        }
    }

    public Skill getSkillData() {
        return skillData;
    }

    public void setSkillData(Skill skillData) {
        this.skillData = skillData;
    }

    public int getOnCooldown() {
        return onCooldown;
    }

    public void setOnCooldown(int onCooldown) {
        this.onCooldown = onCooldown;
    }

    public void sumToCooldown(double toSum) {
        onCooldown += toSum;
        if (onCooldown <= 0) {
            onCooldown = 0;
        }
    }

    public void cooldownDecrement() {
        onCooldown -= 1;
        if (onCooldown <= 0) {
            onCooldown = 0;
        }
    }

    public void cooldownIncrement() {
        onCooldown += 1;
        if (onCooldown <= 0) {
            onCooldown = 0;
        }
    }

    public boolean isReserving() {
        return reserving;
    }

    public void setReserving(boolean reserving) {
        this.reserving = reserving;
    }

    public void refreshIsActive() {
        skillData.setIsActive(reserving);
    }

    public SkillInstance deepcopy() {
        SkillInstance copy = new SkillInstance();

        copy.skillData = this.skillData;

        copy.instanceBundle = this.instanceBundle.deepcopy();

        copy.onCooldown = this.onCooldown;
        copy.reserving = this.reserving;

        return copy;
    }

    public ModifierBundle getInstanceBundle() {
        return instanceBundle;
    }

    public void setInstanceBundle(ModifierBundle instanceBundle) {
        this.instanceBundle = instanceBundle;
    }

}
