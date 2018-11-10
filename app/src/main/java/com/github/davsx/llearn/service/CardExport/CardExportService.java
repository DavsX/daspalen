package com.github.davsx.llearn.service.CardExport;

import com.github.davsx.llearn.LLearnConstants;
import com.github.davsx.llearn.persistence.entity.CardEntityOld;
import com.github.davsx.llearn.persistence.entity.JournalEntity;
import com.github.davsx.llearn.persistence.repository.CardRepositoryOld;
import com.github.davsx.llearn.persistence.repository.JournalRepository;
import com.github.davsx.llearn.service.CardImage.CardImageService;
import com.github.davsx.llearn.service.Settings.SettingsService;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CardExportService {

    private StringWriter csvStringWriter;
    private CSVWriter csvWriter;
    private ZipOutputStream zipOutputStream;

    private CardRepositoryOld cardRepository;
    private JournalRepository journalRepository;
    private CardImageService cardImageService;
    private SettingsService settingsService;

    private int maxProgress = 0;
    private int currentProgress = 0;
    private String status;
    private Long maxCardId;
    private Long maxJournalId;
    private List<File> imageFiles;

    private ExportStatus exportStatus = ExportStatus.EXPORT_NOT_RUNNING;

    public CardExportService(CardRepositoryOld cardRepository, JournalRepository journalRepository,
                             CardImageService cardImageService, SettingsService settingsService) {
        this.cardRepository = cardRepository;
        this.journalRepository = journalRepository;
        this.cardImageService = cardImageService;
        this.settingsService = settingsService;
    }

    public void startExport(OutputStream outputStream) {
        status = "Starting export...";
        imageFiles = cardImageService.getAllFiles();

        csvStringWriter = new StringWriter();
        csvWriter = new CSVWriter(csvStringWriter);
        zipOutputStream = new ZipOutputStream(outputStream);
        maxCardId = 0L;
        maxJournalId = 0L;

        maxProgress = cardRepository.allCardsCount()
                + journalRepository.allJournalsCount()
                + imageFiles.size()
                + 1;
        currentProgress = 0;

        exportStatus = ExportStatus.EXPORTING_SETTINGS;
    }

    public void cancelExport() {
        if (csvStringWriter != null) {
            try {
                csvStringWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                csvStringWriter = null;
            }
        }
        if (csvWriter != null) {
            try {
                csvWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                csvWriter = null;
            }
        }
        if (zipOutputStream != null) {
            try {
                zipOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                zipOutputStream = null;
            }
        }
        exportStatus = ExportStatus.EXPORT_NOT_RUNNING;
        status = "Export cancelled";
        maxCardId = 0L;
        maxJournalId = 0L;
    }

    public boolean doNextChunk() {
        if (exportStatus.equals(ExportStatus.EXPORTING_SETTINGS)) {
            return doChunkExportSettings();
        } else if (exportStatus.equals(ExportStatus.SAVE_SETTINGS_TO_ZIP)) {
            return doChunkSaveSettingsToZip();
        } else if (exportStatus.equals(ExportStatus.EXPORTING_CARDS)) {
            return doChunkExportCards();
        } else if (exportStatus.equals(ExportStatus.SAVE_CARDS_TO_ZIP)) {
            return doChunkSaveCardsToZip();
        } else if (exportStatus.equals(ExportStatus.EXPORTING_JOURNAL)) {
            return doChunkExportJournals();
        } else if (exportStatus.equals(ExportStatus.SAVE_JOURNALS_TO_ZIP)) {
            return doChunkSaveJournalsToZip();
        } else if (exportStatus.equals(ExportStatus.EXPORTING_IMAGES)) {
            return doChunkExportImages();
        } else if (exportStatus.equals(ExportStatus.FINISHED)) {
            return doChunkFinished();
        }
        return false;
    }

    private boolean doChunkExportSettings() {
        status = "Exporting settings";

        List<String[]> lines = settingsService.toCsvDataV1();
        for (String[] line : lines) {
            csvWriter.writeNext(line);
        }

        exportStatus = ExportStatus.SAVE_SETTINGS_TO_ZIP;
        csvStringWriter.flush();

        return true;
    }

    private boolean doChunkSaveSettingsToZip() {
        status = "Saving settings to ZIP";

        try {
            String fileName = "settings_V1.csv";
            byte[] bytes = csvStringWriter.toString().getBytes(StandardCharsets.UTF_8);
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            zipOutputStream.write(bytes);
            csvStringWriter.close();
            csvWriter.close();

            csvStringWriter = new StringWriter();
            csvWriter = new CSVWriter(csvStringWriter);
            exportStatus = ExportStatus.EXPORTING_CARDS;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            exportStatus = ExportStatus.ERROR;
            status = "Could not save settings csv to zip";
            return false;
        }
    }

    private boolean doChunkExportCards() {
        status = "Exporting cards";

        int chunkSize = 100;
        List<CardEntityOld> cards = cardRepository.getCardsChunked(maxCardId,
                LLearnConstants.CARD_TYPES_ALL, chunkSize);
        for (CardEntityOld card : cards) {
            currentProgress++;
            csvWriter.writeNext(card.toCsvDataV2());
            maxCardId = card.getId();
        }

        if (cards.size() == 0) {
            exportStatus = ExportStatus.SAVE_CARDS_TO_ZIP;
            csvStringWriter.flush();
        }
        return true;
    }

    private boolean doChunkSaveCardsToZip() {
        status = "Saving cards to ZIP";

        try {
            String fileName = "cards_export_V2.csv";
            byte[] bytes = csvStringWriter.toString().getBytes(StandardCharsets.UTF_8);
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            zipOutputStream.write(bytes);
            csvStringWriter.close();
            csvWriter.close();

            csvStringWriter = new StringWriter();
            csvWriter = new CSVWriter(csvStringWriter);
            exportStatus = ExportStatus.EXPORTING_JOURNAL;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            exportStatus = ExportStatus.ERROR;
            status = "Could not save cards csv to zip";
            return false;
        }
    }

    private boolean doChunkExportJournals() {
        status = "Exporting journals";

        int chunkSize = 100;
        List<JournalEntity> journals =
                journalRepository.getJournalsChunked(maxJournalId, chunkSize);
        for (JournalEntity journal : journals) {
            currentProgress++;
            csvWriter.writeNext(journal.toCsvDataV1());
            maxJournalId = journal.getId();
        }

        if (journals.size() == 0) {
            exportStatus = ExportStatus.SAVE_JOURNALS_TO_ZIP;
            csvStringWriter.flush();
        }
        return true;
    }

    private boolean doChunkSaveJournalsToZip() {
        status = "Saving journals to ZIP";

        try {
            String fileName = "journals_export_V1.csv";
            byte[] bytes = csvStringWriter.toString().getBytes(StandardCharsets.UTF_8);
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            zipOutputStream.write(bytes);
            csvStringWriter.close();
            csvWriter.close();

            exportStatus = ExportStatus.EXPORTING_IMAGES;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            exportStatus = ExportStatus.ERROR;
            status = "Could not save journals csv to zip";
            return false;
        }
    }

    private boolean doChunkFinished() {
        status = "Export finished";

        try {
            zipOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            exportStatus = ExportStatus.ERROR;
            status = "Could close zip file";
            return false;
        }

        currentProgress++;

        return false;
    }

    private boolean doChunkExportImages() {
        status = "Exporting images";

        try {
            if (imageFiles.size() > 0) {
                File img = imageFiles.remove(0);
                currentProgress++;

                zipOutputStream.putNextEntry(new ZipEntry(img.getName()));
                FileInputStream imgInputStream = new FileInputStream(img);

                int length;
                byte[] buffer = new byte[1024];
                while ((length = imgInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, length);
                }
                imgInputStream.close();
            } else {
                exportStatus = ExportStatus.FINISHED;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            exportStatus = ExportStatus.ERROR;
            status = "Could not save cards csv to zip";
            return false;
        }
    }

    private enum ExportStatus {
        EXPORT_NOT_RUNNING,
        EXPORTING_SETTINGS,
        SAVE_SETTINGS_TO_ZIP,
        EXPORTING_CARDS,
        SAVE_CARDS_TO_ZIP,
        EXPORTING_JOURNAL,
        SAVE_JOURNALS_TO_ZIP,
        EXPORTING_IMAGES,
        FINISHED,
        ERROR
    }

    public int getCurrentProgress() {
        return (this.currentProgress * 100) / this.maxProgress;
    }

    public String getDefaultFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = formatter.format(new Date(System.currentTimeMillis()));
        return "LLearn_backup_" + date + ".zip";
    }

    public boolean getFinished() {
        return exportStatus.equals(ExportStatus.FINISHED);
    }

    public String getStatus() {
        return this.status;
    }

}
