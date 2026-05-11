package model.entity;

import model.type.UniqueType;

public class UniqueModifier {

    private UniqueType name;
    private boolean active;
    private ModifierBundle modifiers = new ModifierBundle();

    public UniqueModifier(UniqueType name) {
        this.name = name;
    }

    public UniqueModifier() {
        name = null;
    }

    public UniqueType getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ModifierBundle getModifiers() {
        return modifiers;
    }

    public void setModifiers(ModifierBundle modifiers) {
        this.modifiers = modifiers;
    }

    public void setName(UniqueType name) {
        this.name = name;
    }
}
