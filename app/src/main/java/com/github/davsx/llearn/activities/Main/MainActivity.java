package com.github.davsx.llearn.activities.Main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.activities.CardExport.CardExportActivity;
import com.github.davsx.llearn.activities.CardImport.CardImportActivity;
import com.github.davsx.llearn.activities.LearnQuiz.LearnQuizActivity;
import com.github.davsx.llearn.activities.ManageCards.ManageCardsActivity;
import com.github.davsx.llearn.activities.MemriseImport.MemriseImportActivity;
import com.github.davsx.llearn.activities.ResetImages.ResetImagesActivity;
import com.github.davsx.llearn.activities.ReviewQuiz.ReviewQuizActivity;
import com.github.davsx.llearn.activities.Settings.SettingsActivity;
import com.github.davsx.llearn.service.MainActivity.MainActivityService;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject
    MainActivityService mainActivityService;

    private Button btnLearnCards;
    private Button btnReviewCards;
    private Button btnManageCards;
    private Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("   LLearn");
        actionBar.setIcon(R.mipmap.ic_launcher_icon);

        btnManageCards = findViewById(R.id.button_manage_cards);
        btnLearnCards = findViewById(R.id.button_learn_cards);
        btnReviewCards = findViewById(R.id.button_review_cards);
        btnSettings = findViewById(R.id.button_settings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export_data:
                MainActivity.this.startActivity(new Intent(MainActivity.this, CardExportActivity.class));
                break;
            case R.id.action_import_data:
                MainActivity.this.startActivity(new Intent(MainActivity.this, CardImportActivity.class));
                break;
            case R.id.action_import_memrise:
                MainActivity.this.startActivity(new Intent(MainActivity.this, MemriseImportActivity.class));
                break;
            case R.id.action_reset_images:
                MainActivity.this.startActivity(new Intent(MainActivity.this, ResetImagesActivity.class));
                break;
            case R.id.action_wipe:
                showWipeConfirmDialog(false);
                break;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateButtons();
    }

    private void updateButtons() {
        btnLearnCards.setText(mainActivityService.getLearnButtonString());
        if (mainActivityService.canLearnCards()) {
            btnLearnCards.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, LearnQuizActivity.class);
                    MainActivity.this.startActivity(i);
                }
            });
        } else {
            btnLearnCards.setOnClickListener(null);
        }

        btnReviewCards.setText(mainActivityService.getReviewButtonString());
        if (mainActivityService.canReviewCards()) {
            btnReviewCards.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, ReviewQuizActivity.class);
                    MainActivity.this.startActivity(i);
                }
            });
        } else {
            btnReviewCards.setOnClickListener(null);
        }

        btnManageCards.setText(mainActivityService.getManageButtonString());
        btnManageCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ManageCardsActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        btnLearnCards.getBackground().setAlpha(45);
        btnReviewCards.getBackground().setAlpha(45);
        btnManageCards.getBackground().setAlpha(45);
        btnSettings.getBackground().setAlpha(45);
    }

    private void showWipeConfirmDialog(final boolean confirmed) {
        String message = confirmed ? "Are you really sure?" : "Are you sure?";
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Wipe data")
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (confirmed) {
                            wipeData();
                        } else {
                            dialog.dismiss();
                            showWipeConfirmDialog(true);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void wipeData() {
        mainActivityService.wipeData();
        updateButtons();
    }

}
