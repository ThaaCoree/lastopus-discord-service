package model.entity.items.catalysts;

import model.entity.items.catalysts.catalyst_list.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class CatalystFactory {
    private final Map<String, CatalystEffect> effects = new LinkedHashMap<>();

    public CatalystFactory() {
        register(new Unstable_Shard());
        register(new Flow_Shard());
        register(new Infusion_Shard());
        register(new White_Flux());
        register(new Small_Flux());
        register(new Synthesizer());
    }

    private void register(CatalystEffect effect) {
        effects.put(effect.getCatalyst_name(), effect);
    }

    public CatalystEffect get(String currencyId) {
        return effects.get(currencyId);
    }
}
