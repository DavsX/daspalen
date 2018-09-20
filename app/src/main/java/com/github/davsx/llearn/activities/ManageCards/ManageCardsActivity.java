package com.github.davsx.llearn.activities.ManageCards;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.github.davsx.llearn.LLearnApplication;
import com.github.davsx.llearn.R;
import com.github.davsx.llearn.service.ManageCards.ManageCardsService;

import javax.inject.Inject;

public class ManageCardsActivity extends AppCompatActivity {

    @Inject
    ManageCardsService manageCardsService;

    private ManageCardsAdapter adapter;
    private MenuItem menuShowIncomplete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cards);

        ((LLearnApplication) getApplication()).getApplicationComponent().inject(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        adapter = new ManageCardsAdapter(this, manageCardsService);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (adapter.onScrolled(linearLayoutManager.findLastVisibleItemPosition())) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manage_cards_menu, menu);

        menuShowIncomplete = menu.findItem(R.id.checkbox_show_incomplete);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.searchCards(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.length() == 0) {
                    adapter.cancelSearch();
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_word:
                showCreateCardDialog();
            case R.id.checkbox_show_incomplete:
                menuShowIncomplete.setChecked(!menuShowIncomplete.isChecked());
                adapter.showOnlyIncomplete(menuShowIncomplete.isChecked());
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        long cardId = intent.getLongExtra("ID_CARD", 0L);
        int cardPosition = intent.getIntExtra("CARD_POSITION", 0);
        int result = intent.getIntExtra("RESULT", ManageCardsService.RESULT_CARD_CHANGED); // Reload card just to be sure

        if (result == ManageCardsService.RESULT_CARD_CHANGED) {
            manageCardsService.cardChanged(cardId, cardPosition);
        } else if (result == ManageCardsService.RESULT_CARD_DELETED) {
            manageCardsService.cardDeleted(cardPosition);
        } else if (result == ManageCardsService.RESULT_CARD_ADDED) {
            manageCardsService.cardAdded();
        }

        adapter.notifyDataSetChanged();
    }

    private void showCreateCardDialog() {
        CreateCardDialog createCardDialog = new CreateCardDialog(ManageCardsActivity.this, manageCardsService);
        createCardDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                ManageCardsActivity.this.adapter.notifyDataSetChanged();
            }
        });
        createCardDialog.show();
    }
}