package manager;

import model.entity.PassiveNode;
import model.entity.units.Unit;

import java.util.*;

public class PassiveManager {
    private final Unit unit;
    private final Map<Integer, Set<Integer>> adjacency = new HashMap<>();
    private int rootId;

    public PassiveManager(Unit unit) {
        this.unit = unit;
        buildAdjacency(unit.getAllocatedPassives());
    }

    public void allocateNode(PassiveNode node) {
        if (canAllocate(node)) {
            unit.getAllocatedPassives().putIfAbsent(node.getId(), node);
            buildAdjacency(unit.getAllocatedPassives());
        }
        unit.calculateEverything();
    }

    public void unallocateNode(PassiveNode node) {
        if (canDeallocate(node)) {
            unit.getAllocatedPassives().remove(node.getId());
            buildAdjacency(unit.getAllocatedPassives());
        }
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

    public boolean canDeallocate(PassiveNode node) {
        Map<Integer, PassiveNode> allocated = unit.getAllocatedPassives();
        int id = node.getId();

        if (!allocated.containsKey(id)) return false;
        if (id == rootId) return false;

        Set<Integer> visited = new HashSet<>();
        Deque<Integer> queue = new ArrayDeque<>();

        // จุดเริ่ม = โหนดที่ลงอยู่และติดกับ root (ในลิสต์ของมันมี rootId)
        for (PassiveNode n : allocated.values()) {
            if (n.getId() == id || n.getId() == rootId) continue;
            List<Integer> conn = n.getConnectedNodes();
            if (conn != null && conn.contains(rootId) && visited.add(n.getId())) {
                queue.add(n.getId());
            }
        }

        // เดินต่อผ่านโหนดที่ลงอยู่ โดยข้ามโหนดที่จะถอน
        while (!queue.isEmpty()) {
            PassiveNode cur = allocated.get(queue.poll());
            List<Integer> conn = cur.getConnectedNodes();
            if (conn == null) continue;
            for (int next : conn) {
                if (next == id || next == rootId) continue;
                if (allocated.containsKey(next) && visited.add(next)) queue.add(next);
            }
        }

        // โหนดที่ลงอยู่ทั้งหมด (ยกเว้นตัวที่จะถอน และ root) ต้องถูกเดินถึงครบ
        for (int allocId : allocated.keySet()) {
            if (allocId == id || allocId == rootId) continue;
            if (!visited.contains(allocId)) return false;
        }
        return true;
    }

    private void buildAdjacency(Map<Integer, PassiveNode> allNodes) {
        adjacency.clear();

        for (PassiveNode n : allNodes.values()) {
            List<Integer> connected = n.getConnectedNodes();

            // โหนดที่ไม่มี connectedNodes คือ root
            if (connected == null || connected.isEmpty()) {
                rootId = n.getId();
                continue;
            }

            for (Integer other : connected) {
                adjacency.computeIfAbsent(n.getId(), k -> new HashSet<>()).add(other);
                adjacency.computeIfAbsent(other, k -> new HashSet<>()).add(n.getId());
            }
        }
    }

    public String applyChanges(List<Integer> added, List<Integer> removed,
                               Map<Integer, PassiveNode> database_passive) {
        Map<Integer, PassiveNode> current = unit.getAllocatedPassives();
        Set<Integer> addSet = new HashSet<>(added == null ? List.of() : added);
        Set<Integer> remSet = new HashSet<>(removed == null ? List.of() : removed);

        int reset_point = unit.getStarmap_reset_point();

        if (remSet.size() > reset_point) return "reset point ไม่พอ";

        if (!Collections.disjoint(addSet, remSet)) return "ข้อมูลซ้ำซ้อน";
        for (int id : remSet) {
            if (!current.containsKey(id)) return "ถอนโหนดที่ยังไม่ได้ลง: " + id;
        }
        for (int id : addSet) {
            if (current.containsKey(id)) return "โหนดนี้ลงไปแล้ว: " + id;
            if (database_passive.get(id) == null) return "ไม่พบโหนด: " + id;
        }

        int cost = addSet.size() - remSet.size();
        if (cost > unit.getRemainingPassiveTreePoint()) return "passive point ไม่พอ";

        // สร้างผังสุดท้ายในสำเนาก่อน ยังไม่แตะของจริง
        Map<Integer, PassiveNode> next = new LinkedHashMap<>(current);
        remSet.forEach(next::remove);
        for (int id : addSet) next.put(id, database_passive.get(id));

        if (!isConnectedToRoot(next)) return "ผังไม่ต่อเนื่อง";

        int before = countRootNeighbors(current);
        int after = countRootNeighbors(next);
        if (after > 1 && after > before) return "ออกจากจุดเริ่มต้นได้ทางเดียวเท่านั้น";

        // ผ่านทั้งหมดแล้วค่อย commit
        current.clear();
        current.putAll(next);
        reset_point -= remSet.size();
        unit.setStarmap_reset_point(reset_point);
        System.out.println("ตัวละคร "+unit.getName()+" ใช้งานแต้มรีเซ็ตแผนที่ดวงดาวไปแล้ว "+remSet.size()+" หน่วย");
        unit.setRemainingPassiveTreePoint(unit.getRemainingPassiveTreePoint() - cost);
        unit.calculateEverything();
        return null;   // null = สำเร็จ
    }

    private boolean isConnectedToRoot(Map<Integer, PassiveNode> state) {
        Integer rootId = null;
        for (PassiveNode n : state.values()) {
            if (isRoot(n)) { rootId = n.getId(); break; }
        }
        if (rootId == null) return false;          // root ถูกถอน หรือไม่มี

        Set<Integer> visited = new HashSet<>();
        Deque<Integer> queue = new ArrayDeque<>();
        for (PassiveNode n : state.values()) {
            if (n.getId() == rootId) continue;
            List<Integer> conn = n.getConnectedNodes();
            if (conn != null && conn.contains(rootId) && visited.add(n.getId())) {
                queue.add(n.getId());
            }
        }
        while (!queue.isEmpty()) {
            PassiveNode cur = state.get(queue.poll());
            List<Integer> conn = cur.getConnectedNodes();
            if (conn == null) continue;
            for (int next : conn) {
                if (next != rootId && state.containsKey(next) && visited.add(next)) {
                    queue.add(next);
                }
            }
        }
        return visited.size() == state.size() - 1;   // ทุกโหนดยกเว้น root ถูกเดินถึง
    }

    private int countRootNeighbors(Map<Integer, PassiveNode> state) {
        Integer rootId = null;
        for (PassiveNode n : state.values()) {
            if (isRoot(n)) { rootId = n.getId(); break; }
        }
        if (rootId == null) return 0;

        int count = 0;
        for (PassiveNode n : state.values()) {
            List<Integer> conn = n.getConnectedNodes();
            if (conn != null && conn.contains(rootId)) count++;
        }
        return count;
    }

    private boolean isRoot(PassiveNode n) {
        return n.getId() == 1;
    }
}
