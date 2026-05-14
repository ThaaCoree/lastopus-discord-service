package app.servicemodel;

import model.entity.items.Rune;

import java.util.List;
import java.util.Map;

public class RuneboardRequest {
    public String player_name;
    public Map<Integer, Rune> rune_inventory;
    public List<Rune> socketed_runes;
}
