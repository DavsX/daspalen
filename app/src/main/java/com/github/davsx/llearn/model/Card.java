package com.github.davsx.llearn.model;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.entity.CardNotificationEntity;
import com.github.davsx.llearn.persistence.entity.CardQuizEntity;

public class Card {

    boolean cardEntityChanged = false;
    boolean cardQuizEntityChanged = false;
    boolean cardNotificationEntityChanged = false;
    private CardEntity cardEntity;
    private CardQuizEntity cardQuizEntity;
    private CardNotificationEntity cardNotificationEntity;

    public Card(CardEntity cardEntity, CardQuizEntity cardQuizEntity, CardNotificationEntity cardNotificationEntity) {
        this.cardEntity = cardEntity;
        this.cardQuizEntity = cardQuizEntity;
        this.cardNotificationEntity = cardNotificationEntity;
    }

    public static Card createFromKindle(String backText) {
        CardEntity cardEntity = new CardEntity()
                .setBackText(backText)
                .setCreatedAt(System.currentTimeMillis())
                .setLocalVersion(1)
                .setSyncedVersion(0);
        CardQuizEntity cardQuizEntity = new CardQuizEntity()
                .setQuizType(LLearnConstants.CARD_TYPE_INCOMPLETE)
                .setCreatedAt(System.currentTimeMillis())
                .setLocalVersion(1)
                .setSyncedVersion(0);
        CardNotificationEntity cardNotificationEntity = new CardNotificationEntity()
                .setCreatedAt(System.currentTimeMillis())
                .setLocalVersion(1)
                .setSyncedVersion(0);

        Card c = new Card(cardEntity, cardQuizEntity, cardNotificationEntity);

        c.cardEntityChanged = true;
        c.cardQuizEntityChanged = true;
        c.cardNotificationEntityChanged = true;

        return c;
    }

    public String getBackText() {
        return cardEntity.getBackText();
    }

    public CardEntity getCardEntity() {
        return cardEntity;
    }

    public long getCardId() {
        return cardEntity.getCardId();
    }

    public void setCardId(long cardId) {
        if (cardEntity.getCardId() > 0) {
            throw new AssertionError("ID already set for Card");
        }

        cardEntity.setCardId(cardId);
        cardQuizEntity.setCardId(cardId);
        cardNotificationEntity.setCardId(cardId);
    }

    public CardNotificationEntity getCardNotificationEntity() {
        return cardNotificationEntity;
    }

    public CardQuizEntity getCardQuizEntity() {
        return cardQuizEntity;
    }

    public boolean getEnabled() {
        return cardEntity.getEnabled();
    }

    public void setEnabled(boolean enabled) {
        cardEntity.setEnabled(enabled);
        cardEntityChanged = true;
    }

    public String getFrontText() {
        return cardEntity.getFrontText();
    }

    public int getLearnScore() {
        return cardQuizEntity.getLearnScore();
    }

    public boolean isCardEntityChanged() {
        return cardEntityChanged;
    }

    public boolean isCardQuizEntityChanged() {
        return cardQuizEntityChanged;
    }

    public boolean isCardNotificationEntityChanged() {
        return cardNotificationEntityChanged;
    }
}
