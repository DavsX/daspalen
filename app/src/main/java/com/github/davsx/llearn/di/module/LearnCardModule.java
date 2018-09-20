package com.github.davsx.llearn.di.module;

import com.github.davsx.llearn.persistence.repository.CardRepository;
import com.github.davsx.llearn.service.LearnQuiz.LearnQuizService;
import dagger.Module;
import dagger.Provides;

@Module(includes = CardRepositoryModule.class)
public class LearnCardModule {
    @Provides
    LearnQuizService provideLearnCardService(CardRepository cardRepository) {
        return new LearnQuizService(cardRepository);
    }

}