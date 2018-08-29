package com.github.davsx.llearn.persistence.repository;

import android.content.Context;
import com.github.davsx.llearn.persistence.LLearnDatabase;
import com.github.davsx.llearn.persistence.dao.CardDao;
import com.github.davsx.llearn.persistence.entity.CardEntity;

import java.util.List;

public class CardRepository {
    private static CardRepository INSTANCE;

    public static CardRepository getInstance(Context context) {
        if (INSTANCE == null) {
            CardDao cardDao = LLearnDatabase.getInstance(context).cardDao();
            INSTANCE = new CardRepository(cardDao);
        }
        return INSTANCE;
    }

    private final CardDao cardDao;

    public CardRepository(CardDao cardDao) {
        this.cardDao = cardDao;
    }

    public List<CardEntity> getAllCards() {
        return cardDao.getAllCards();
    }

    public CardEntity getCardWithId(Long id_card) {
        return cardDao.getCardWithId(id_card);
    }

    public CardEntity getCardWithFront(String front) {
        return cardDao.getCardWithFront(front);
    }

    public Long save(CardEntity card) {
        return cardDao.save(card);
    }

    public Integer cardCount() {
        return cardDao.cardCount();
    }

    public void deleteCard(CardEntity card) {
        cardDao.delete(card);
    }
}
