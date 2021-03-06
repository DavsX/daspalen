package com.github.davsx.daspalen.activities.MemriseImport;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.*;
import com.github.davsx.daspalen.DaspalenApplication;
import com.github.davsx.daspalen.DaspalenConstants;
import com.github.davsx.daspalen.R;
import com.github.davsx.daspalen.persistence.entity.CardEntity;
import com.github.davsx.daspalen.service.MemriseImport.MemriseImportService;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MemriseImportActivity extends Activity {

    @Inject
    MemriseImportService memriseImportService;

    private Button buttonChooseFile;
    private EditText editTextFront;
    private EditText editTextBack;
    private Button buttonSkip;
    private Button buttonSave;
    private Button buttonReset;
    private Button buttonAll;
    private ProgressBar progressBar;
    private ImageView buttonSwap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memrise_import);

        ((DaspalenApplication) getApplication()).getApplicationComponent().inject(this);

        buttonChooseFile = findViewById(R.id.button_choose_file);
        buttonSave = findViewById(R.id.button_save);
        buttonSkip = findViewById(R.id.button_skip);
        buttonReset = findViewById(R.id.button_reset);
        buttonAll = findViewById(R.id.button_load_all);
        editTextFront = findViewById(R.id.edittext_front);
        editTextBack = findViewById(R.id.edittext_back);
        progressBar = findViewById(R.id.progress_bar);
        buttonSwap = findViewById(R.id.button_swap_front_back);

        progressBar.setMax(100);
        progressBar.setProgress(0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        buttonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("text/tab-separated-values");
                startActivityForResult(i, DaspalenConstants.REQUEST_CODE_OPEN_DOCUMENT);
            }
        });
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memriseImportService.skipCard();
                showNextCard();
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (createCard()) {
                    showNextCard();
                }
            }
        });
        buttonSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String frontText = editTextFront.getText().toString();
                String backText = editTextBack.getText().toString();

                editTextFront.setText(backText);
                editTextBack.setText(frontText);
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextCard();
            }
        });
        buttonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAllCards();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == DaspalenConstants.REQUEST_CODE_OPEN_DOCUMENT) {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        startImport(uri);
                    }
                }
            }
        }
    }

    private boolean createCard() {
        String frontText = editTextFront.getText().toString();
        String backText = editTextBack.getText().toString();

        if (frontText.length() == 0 && backText.length() == 0) {
            return false;
        }

        CardEntity duplicateCard = memriseImportService.findDuplicateCardEntity(frontText, backText);
        if (duplicateCard == null) {
            memriseImportService.createCard(frontText, backText);
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String dialogMessage = String.format(
                    "The current card cannot be saved, because a duplicate card was found:<br /><br />Front<br " +
                            "/><b>%s</b><br />Back<br /><b>%s</b>",
                    duplicateCard.getFrontText(),
                    duplicateCard.getBackText());
            builder.setTitle("Error")
                    .setMessage(Html.fromHtml(dialogMessage))
                    .setPositiveButton("Ok", null)
                    .show();
        }
        return false;
    }

    private void loadAllCards() {
        while (createCard()) {
            showNextCard();
        }
    }

    private void showNextCard() {
        Pair<String, String> nextCard = memriseImportService.getNextCard();
        if (nextCard != null) {
            editTextFront.setText(nextCard.first);
            editTextBack.setText(nextCard.second);
            progressBar.setProgress(memriseImportService.getProgress());
        }
    }

    private void startImport(Uri uri) {
        InputStream inputStream;
        try {
            inputStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not open file", Toast.LENGTH_SHORT).show();
            return;
        }

        memriseImportService.startImport(inputStream);
        showNextCard();
    }

}
