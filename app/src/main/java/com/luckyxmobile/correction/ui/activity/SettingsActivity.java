package com.luckyxmobile.correction.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.CheckBoxPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.global.Constants;
import com.luckyxmobile.correction.global.MySharedPreferences;
import com.luckyxmobile.correction.utils.FilesUtils;
import com.luckyxmobile.correction.utils.ImageTask;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    public static class SettingsFragment extends PreferenceFragmentCompat{

        private MySharedPreferences preferences = MySharedPreferences.getInstance();

        private PreferenceScreen setTagPre;
        private PreferenceScreen clearPre;
        private MultiSelectListPreference printPagePre;
        private CheckBoxPreference fullScreenViewPagePre;
        private CheckBoxPreference showTagViewPagePre;
        private CheckBoxPreference hideHighlightPre;
        private int[] PrintContent;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_setting, rootKey);

            PrintContent = new int[]{R.string.stem, R.string.correct, R.string.incorrect, R.string.key, R.string.cause};

            setTagPre = findPreference("set_tag_pre");
            clearPre = findPreference("clear_pre");
            printPagePre = findPreference("print_paper_pre");
            fullScreenViewPagePre =  findPreference("view_pager_full_screen");
            showTagViewPagePre =  findPreference("view_pager_show_tag");
            hideHighlightPre = findPreference("print_page_show_smear");
            PreferenceScreen versionPre = findPreference("version_pre");
            versionPre.setSummary(FilesUtils.getInstance().appVersionName());

            hideHighlightPre.setChecked(preferences.getBoolean(Constants.PRINT_HIDE_HIGHLIGHTER,true));
            showTagViewPagePre.setChecked(preferences.getBoolean(Constants.SHOW_TAG_IN_TOPIC_VIEW_PAGE,true));
            fullScreenViewPagePre.setChecked(preferences.getBoolean(Constants.VIEW_PAGE_FULL_SCREEN,false));

            setPrintPage();
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {

            if (preference==setTagPre){
                startActivity(new Intent(getActivity(), TagManagerActivity.class));
            }else if (preference==showTagViewPagePre){
                preferences.putBoolean(Constants.SHOW_TAG_IN_TOPIC_VIEW_PAGE,showTagViewPagePre.isChecked());
            }else if (preference==fullScreenViewPagePre){
                preferences.putBoolean(Constants.VIEW_PAGE_FULL_SCREEN,fullScreenViewPagePre.isChecked());
            }else if (preference== hideHighlightPre){
                preferences.putBoolean(Constants.PRINT_HIDE_HIGHLIGHTER, hideHighlightPre.isChecked());
            } else if (preference ==clearPre) {
                FilesUtils.getInstance().deleteCache();
            }
            return super.onPreferenceTreeClick(preference);
        }

        private void setPrintPage() {

            String[] p = Objects.requireNonNull(preferences.getString(Constants.TABLE_PRINT_PAGE, "0")).split(",");

            printPagePre.setSummary(intToString(PrintContent, p));

            printPagePre.setOnPreferenceChangeListener((preference, newValue) -> {

                String selectPrint = newValue.toString().replace("[","").replace("]","").replace(" ","");

                if (TextUtils.isEmpty(selectPrint)){
                    Toast.makeText(getContext(), R.string.select_at_least_one, Toast.LENGTH_SHORT).show();
                    return false;
                }else if(!selectPrint.contains("0")){
                    Toast.makeText(getContext(), R.string.have_to_select_stem, Toast.LENGTH_SHORT).show();
                    return  false;
                }else{
                    preferences.putString(Constants.TABLE_PRINT_PAGE,selectPrint);
                    preference.setSummary(intToString(PrintContent, selectPrint.split(",")));
                    return true;
                }

            });

        }

        private String intToString(int[] integerArray, String[] sArray){

            StringBuilder summary = new StringBuilder();
            String s = "";

            for (int i = 0; i < sArray.length; i++) {
                s = getString(integerArray[Integer.parseInt(sArray[i])]);
                if (i==0){
                    summary.append(s);
                }else{
                    summary.append(", ").append(s);
                }
            }
            return summary.toString();
        }
    }

}