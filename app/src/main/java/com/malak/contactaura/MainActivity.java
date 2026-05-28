package com.malak.contactaura;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnImportLocal;
    private Button btnSendCloud;
    private Button btnRemoteSearch;

    private EditText etRemoteKeyword;
    private TextView tvAuraStatus;
    private ProgressBar auraProgress;

    private RecyclerView rvAuraContacts;
    private PeopleAdapter peopleAdapter;

    private final List<PhonePerson> auraPeople = new ArrayList<>();
    private DirectoryApi directoryApi;

    private final ActivityResultLauncher<String> contactPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    loadPhoneContacts();
                } else {
                    showMessage("Permission contacts refusée");
                    updateStatus("Impossible de lire les contacts sans permission");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        prepareRecyclerView();
        prepareApi();
        prepareClicks();

        updateStatus("Prêt à charger les contacts");
    }

    private void bindViews() {
        btnImportLocal = findViewById(R.id.btnImportLocal);
        btnSendCloud = findViewById(R.id.btnSendCloud);
        btnRemoteSearch = findViewById(R.id.btnRemoteSearch);

        etRemoteKeyword = findViewById(R.id.etRemoteKeyword);
        tvAuraStatus = findViewById(R.id.tvAuraStatus);
        auraProgress = findViewById(R.id.auraProgress);

        rvAuraContacts = findViewById(R.id.rvAuraContacts);
    }

    private void prepareRecyclerView() {
        peopleAdapter = new PeopleAdapter(auraPeople);
        rvAuraContacts.setLayoutManager(new LinearLayoutManager(this));
        rvAuraContacts.setAdapter(peopleAdapter);
    }

    private void prepareApi() {
        directoryApi = ApiHub.getRetrofit().create(DirectoryApi.class);
    }

    private void prepareClicks() {
        btnImportLocal.setOnClickListener(v -> verifyPermissionThenLoad());
        btnSendCloud.setOnClickListener(v -> syncVisibleContacts());
        btnRemoteSearch.setOnClickListener(v -> searchFromServer());
    }

    private void verifyPermissionThenLoad() {
        boolean permissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED;

        if (permissionGranted) {
            loadPhoneContacts();
        } else {
            contactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
    }

    private void loadPhoneContacts() {
        setLoading(true);
        auraPeople.clear();

        Set<String> alreadyAddedNumbers = new HashSet<>();

        String[] columns = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                columns,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            try {
                int nameColumn = cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                );

                int phoneColumn = cursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                );

                while (cursor.moveToNext()) {
                    String rawName = cursor.getString(nameColumn);
                    String rawPhone = cursor.getString(phoneColumn);

                    String cleanName = cleanName(rawName);
                    String cleanPhone = normalizePhone(rawPhone);

                    if (!cleanPhone.isEmpty() && !alreadyAddedNumbers.contains(cleanPhone)) {
                        auraPeople.add(new PhonePerson(cleanName, cleanPhone));
                        alreadyAddedNumbers.add(cleanPhone);
                    }
                }
            } finally {
                cursor.close();
            }
        }

        peopleAdapter.refreshWith(auraPeople);
        setLoading(false);

        updateStatus(auraPeople.size() + " contact(s) chargés depuis le téléphone");
        showMessage("Contacts chargés : " + auraPeople.size());
    }

    private void syncVisibleContacts() {
        if (auraPeople.isEmpty()) {
            showMessage("Charge d'abord les contacts");
            updateStatus("Aucun contact à synchroniser");
            return;
        }

        setLoading(true);
        updateStatus("Synchronisation en cours...");

        final int total = auraPeople.size();
        final int[] completed = {0};
        final int[] successCount = {0};
        final int[] failCount = {0};

        for (PhonePerson person : auraPeople) {
            directoryApi.uploadPerson(person).enqueue(new Callback<ServerReply>() {
                @Override
                public void onResponse(@NonNull Call<ServerReply> call,
                                       @NonNull Response<ServerReply> response) {

                    completed[0]++;

                    if (response.isSuccessful()
                            && response.body() != null
                            && response.body().isSuccess()) {
                        successCount[0]++;
                    } else {
                        failCount[0]++;
                    }

                    finishOneSyncIfNeeded(completed[0], total, successCount[0], failCount[0]);
                }

                @Override
                public void onFailure(@NonNull Call<ServerReply> call,
                                      @NonNull Throwable t) {

                    completed[0]++;
                    failCount[0]++;

                    finishOneSyncIfNeeded(completed[0], total, successCount[0], failCount[0]);
                }
            });
        }
    }

    private void finishOneSyncIfNeeded(int completed, int total, int success, int failed) {
        updateStatus("Synchronisation : " + completed + "/" + total);

        if (completed == total) {
            setLoading(false);

            String summary = success + " réussi(s), " + failed + " échoué(s)";
            updateStatus("Synchronisation terminée : " + summary);
            showMessage(summary);
        }
    }

    private void searchFromServer() {
        String keyword = etRemoteKeyword.getText().toString().trim();

        if (keyword.isEmpty()) {
            showMessage("Saisis un nom ou un numéro");
            updateStatus("Recherche vide ignorée");
            return;
        }

        setLoading(true);
        updateStatus("Recherche distante en cours...");

        directoryApi.searchRemotePeople(keyword).enqueue(new Callback<List<PhonePerson>>() {
            @Override
            public void onResponse(@NonNull Call<List<PhonePerson>> call,
                                   @NonNull Response<List<PhonePerson>> response) {

                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<PhonePerson> results = response.body();
                    peopleAdapter.refreshWith(results);

                    updateStatus(results.size() + " résultat(s) trouvé(s)");
                    showMessage("Résultats : " + results.size());
                } else {
                    updateStatus("Réponse serveur invalide");
                    showMessage("Erreur serveur");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PhonePerson>> call,
                                  @NonNull Throwable t) {

                setLoading(false);
                updateStatus("Impossible de contacter le serveur");
                showMessage("Erreur réseau : vérifie l'adresse IP");
            }
        });
    }

    private String cleanName(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Contact sans nom";
        }

        return value.trim();
    }

    private String normalizePhone(String value) {
        if (value == null) {
            return "";
        }

        return value.replaceAll("[^0-9+]", "");
    }

    private void updateStatus(String text) {
        tvAuraStatus.setText(text);
    }

    private void setLoading(boolean loading) {
        auraProgress.setVisibility(loading ? View.VISIBLE : View.GONE);

        btnImportLocal.setEnabled(!loading);
        btnSendCloud.setEnabled(!loading);
        btnRemoteSearch.setEnabled(!loading);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}