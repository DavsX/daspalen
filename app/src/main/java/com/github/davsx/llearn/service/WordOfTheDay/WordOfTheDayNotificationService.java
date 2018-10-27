package com.github.davsx.llearn.service.WordOfTheDay;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.persistence.entity.CardEntity;
import com.github.davsx.llearn.persistence.repository.CardRepository;

import javax.inject.Inject;
import java.util.List;

public class WordOfTheDayNotificationService extends BroadcastReceiver {

    @Inject
    CardRepository cardRepository;
    @Inject
    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(LLearnConstants.WORD_OF_THE_DAY_INTENT)) {
            ((LLearnApplication) context).getApplicationComponent().inject(this);
            showNotification(context);
        }
    }

    private void showNotification(Context context) {
        List<CardEntity> cards = cardRepository.getRandomCards(1);
        if (cards.size() == 0) {
            return;
        }

        CardEntity card = cards.get(0);
        String message = String.format("<b>Front:</b> %s<br /><b>Back:</b> %s", card.getFront(), card.getBack());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                LLearnConstants.WORD_OF_THE_DAY_NOTIFICATION_CHANNEL);
        builder.setSmallIcon(R.mipmap.ic_launcher_icon);
        builder.setContentTitle("Word Of The Day")
                .setStyle(new NotificationCompat.BigTextStyle(builder).bigText(Html.fromHtml(message)))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(false);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(
                    LLearnConstants.WORD_OF_THE_DAY_NOTIFICATION_CHANNEL,
                    LLearnConstants.WORD_OF_THE_DAY_NOTIFICATION_CHANNEL, importance);
            notificationChannel.enableVibration(true);
            builder.setChannelId(LLearnConstants.WORD_OF_THE_DAY_NOTIFICATION_CHANNEL);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build());

        WordOfTheDayAlarmService.setNextAlarm(context, sharedPreferences);
    }

}
