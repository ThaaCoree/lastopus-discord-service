package manager;

import model.entity.Card;
import model.entity.units.Unit;
import model.type.CardType;

public class CardManager {

    Unit unit;

    public CardManager(Unit unit) {
        this.unit = unit;
    }

    public void replaceCard(CardType slot, Card cardToPut) {
        unit.getCard().put(slot, cardToPut);
        unit.calculateEverything();
    }

    public void unequipCard(CardType slot) {
        unit.getCard().remove(slot);
        unit.calculateEverything();
    }
}
