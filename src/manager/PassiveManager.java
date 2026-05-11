package manager;

import model.entity.PassiveNode;
import model.entity.units.Unit;

public class PassiveManager {
    private final Unit unit;

    public PassiveManager(Unit unit) {
        this.unit = unit;
    }

    public void allocateNode(PassiveNode node) {
        if (canAllocate(node))
        unit.getAllocatedPassives().putIfAbsent(node.getId(), node);
        unit.calculateEverything();
    }

    public void unallocateNode(PassiveNode node) {
        unit.getAllocatedPassives().remove(node.getId());
        unit.calculateEverything();
    }

    public boolean canAllocate(PassiveNode node) {
        if (node.getConnectedNodes() == null || node.getConnectedNodes().isEmpty()) {
            return true;
        } else for (Integer connectedId : node.getConnectedNodes()) {
                if (unit.getAllocatedPassives().containsKey(connectedId)) {
                    return true;
                }
            }
        System.out.println("Cannot allocated "+node.getId()+" because connected node is not allocated.");
        return false;
    }
}
