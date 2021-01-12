package com.luckyxmobile.correction.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.CheckBoxPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.luckyxmobile.correction.R;
import com.luckyxmobile.correction.utils.ConstantsUtil;
import com.luckyxmobile.correction.utils.impl.FilesUtils;
import java.util.Objects;
import es.dmoral.toasty.Toasty;

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

        private SharedPreferences preferences;
        private SharedPreferences.Editor editor;

        private PreferenceScreen setTagPre;
        private MultiSelectListPreference printPagePre;
        private MultiSelectListPreference showSmearPre;
        private MultiSelectListPreference viewSmearByPre;
        private CheckBoxPreference fullScreenViewPagePre;
        private CheckBoxPreference showTagViewPagePre;
        private CheckBoxPreference printSmearPre;
        private PreferenceScreen versionPre;
        private int[] PrintContent;
        private int[] ShowSmear ;
        private int[] ViewSmearContentMethod;


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_setting, rootKey);

            PrintContent = new int[]{R.string.stem, R.string.correct, R.string.incorrect, R.string.key, R.string.cause};
            ShowSmear = new int[]{R.string.correct, R.string.incorrect, R.string.key, R.string.cause};
            ViewSmearContentMethod = new int[]{R.string.click_highlighter_by, R.string.click_button_by};

            setTagPre = findPreference("set_tag_pre");
            printPagePre = findPreference("print_paper_pre");
            showSmearPre = findPreference("show_smear_pre");
            viewSmearByPre = findPreference("view_smear_by");
            fullScreenViewPagePre =  findPreference("view_pager_full_screen");
            showTagViewPagePre =  findPreference("view_pager_show_tag");
            printSmearPre = findPreference("print_page_show_smear");
            versionPre =  findPreference("version_pre");

            preferences = getActivity().getSharedPreferences(ConstantsUtil.TABLE_SHARED_CORRECTION,MODE_PRIVATE);

            setPrintPage();

            setShowSmear();

            setViewSmearBy();

            printSmearPre.setChecked(preferences.getBoolean(ConstantsUtil.TABLE_SHOW_SMEAR_MARK,false));

            showTagViewPagePre.setChecked(preferences.getBoolean(ConstantsUtil.TABLE_SHOW_TAG,true));

            fullScreenViewPagePre.setChecked(preferences.getBoolean(ConstantsUtil.TABLE_FULL_SCREEN,false));

            versionPre.setSummary(FilesUtils.packageName(getContext()));

        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {

            editor = preferences.edit();

            if (preference==setTagPre){
                startActivity(new Intent(getActivity(),SetTagActivity.class));
            }else if (preference==showTagViewPagePre){
                editor.putBoolean(ConstantsUtil.TABLE_SHOW_TAG,showTagViewPagePre.isChecked());
                editor.apply();
            }else if (preference==fullScreenViewPagePre){
                editor.putBoolean(ConstantsUtil.TABLE_FULL_SCREEN,fullScreenViewPagePre.isChecked());
                editor.apply();
            }else if (preference==printSmearPre){
                editor.putBoolean(ConstantsUtil.TABLE_SHOW_SMEAR_MARK,printSmearPre.isChecked());
                editor.apply();
            }

            return super.onPreferenceTreeClick(preference);
        }

        private void setViewSmearBy() {

            String[] p = Objects.requireNonNull(preferences.getString(ConstantsUtil.TABLE_VIEW_SMEAR_BY, "0,1")).split(",");

            viewSmearByPre.setSummary(intToString(ViewSmearContentMethod, p));

            viewSmearByPre.setOnPreferenceChangeListener((preference, newValue) -> {


                String selectViewSmearBy = newValue.toString().replace("[","").replace("]","").replace(" ","");

                if (TextUtils.isEmpty(selectViewSmearBy)){
                    Toasty.error(getContext(), R.string.select_at_least_one,Toasty.LENGTH_SHORT,true).show();
                    return false;
                }else{
                    editor.putString(ConstantsUtil.TABLE_VIEW_SMEAR_BY,selectViewSmearBy);
                    editor.apply();
                    preference.setSummary(intToString(ViewSmearContentMethod, selectViewSmearBy.split(",")));
                    return true;
                }
            });
        }

        private void setShowSmear() {

            String[] p = Objects.requireNonNull(preferences.getString(ConstantsUtil.TABLE_SHOW_SMEAR, "-1")).split(",");

            if (p.length == 1 && p[0].equals("-1")){
                showSmearPre.setSummary(getString(R.string.show_nothing));
            }else{
                showSmearPre.setSummary(intToString(ShowSmear,p));
            }

            showSmearPre.setOnPreferenceChangeListener((preference, newValue) -> {
                String selectPrint = newValue.toString().replace("[","").replace("]","").replace(" ","");

                if (TextUtils.isEmpty(selectPrint)){
                    preference.setSummary(getString(R.string.show_nothing));
                    editor.putString(ConstantsUtil.TABLE_SHOW_SMEAR,"-1");
                }else{
                    editor.putString(ConstantsUtil.TABLE_SHOW_SMEAR,selectPrint);
                    preference.setSummary(intToString(ShowSmear, selectPrint.split(",")));
                }
                editor.apply();

                return true;

            });
        }

        private void setPrintPage() {

            String[] p = Objects.requireNonNull(preferences.getString(ConstantsUtil.TABLE_PRINT_PAGE, "0")).split(",");

            printPagePre.setSummary(intToString(PrintContent, p));

            printPagePre.setOnPreferenceChangeListener((preference, newValue) -> {

                String selectPrint = newValue.toString().replace("[","").replace("]","").replace(" ","");

                if (TextUtils.isEmpty(selectPrint)){
                    Toasty.error(getContext(), R.string.select_at_least_one,Toasty.LENGTH_SHORT,true).show();
                    return false;
                }else if(!selectPrint.contains("0")){
                    Toasty.error(getContext(),R.string.have_to_select_stem, Toasty.LENGTH_SHORT, true).show();
                    return  false;
                }else{
                    editor.putString(ConstantsUtil.TABLE_PRINT_PAGE,selectPrint);
                    editor.apply();
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