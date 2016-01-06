package sample;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by xiaopan on 2016-01-06.
 */
public class CardInfo {
    private SimpleStringProperty cardId = new SimpleStringProperty();
    private SimpleStringProperty id = new SimpleStringProperty();

    public CardInfo(String id, String cardId) {
        this.cardId.set(cardId);
        this.id.set(id);
    }

    public CardInfo() {
    }

    public String getCardId() {
        return cardId.get();
    }

    public SimpleStringProperty cardIdProperty() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId.set(cardId);
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardInfo cardInfo = (CardInfo) o;

        return !(cardId != null ? !cardId.get().equals(cardInfo.cardId.get()) : cardInfo.cardId.get() != null);

    }

    @Override
    public int hashCode() {
        return cardId.get() != null ? cardId.get().hashCode() : 0;
    }
}
