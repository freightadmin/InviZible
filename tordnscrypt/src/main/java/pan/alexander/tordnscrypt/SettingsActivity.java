package pan.alexander.tordnscrypt;
/*
    This file is part of InviZible Pro.

    InviZible Pro is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    InviZible Pro is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with InviZible Pro.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2019-2020 by Garmatin Oleksandr invizible.soft@gmail.com
*/

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Objects;

import pan.alexander.tordnscrypt.dialogs.progressDialogs.PleaseWaitProgressDialog;
import pan.alexander.tordnscrypt.proxy.ProxyFragment;
import pan.alexander.tordnscrypt.settings.PathVars;
import pan.alexander.tordnscrypt.settings.PreferencesCommonFragment;
import pan.alexander.tordnscrypt.settings.PreferencesFastFragment;
import pan.alexander.tordnscrypt.settings.dnscrypt_settings.PreferencesDNSFragment;
import pan.alexander.tordnscrypt.settings.tor_bridges.PreferencesTorBridges;
import pan.alexander.tordnscrypt.settings.SettingsParser;
import pan.alexander.tordnscrypt.settings.ShowLogFragment;
import pan.alexander.tordnscrypt.settings.show_rules.ShowRulesRecycleFrag;
import pan.alexander.tordnscrypt.settings.tor_apps.UnlockTorAppsFragment;
import pan.alexander.tordnscrypt.settings.tor_ips.UnlockTorIpsFrag;
import pan.alexander.tordnscrypt.settings.tor_preferences.PreferencesTorFragment;
import pan.alexander.tordnscrypt.utils.enums.DNSCryptRulesVariant;
import pan.alexander.tordnscrypt.utils.file_operations.FileOperations;

import static pan.alexander.tordnscrypt.utils.RootExecService.LOG_TAG;


public class SettingsActivity extends LangAppCompatActivity {

    public static final String dnscrypt_proxy_toml_tag = "pan.alexander.tordnscrypt/app_data/dnscrypt-proxy/dnscrypt-proxy.toml";
    public static final String tor_conf_tag = "pan.alexander.tordnscrypt/app_data/tor/tor.conf";
    public static final String itpd_conf_tag = "pan.alexander.tordnscrypt/app_data/itpd/itpd.conf";
    public static final String itpd_tunnels_tag = "pan.alexander.tordnscrypt/app_data/itpd/tunnels.conf";
    public static final String public_resolvers_md_tag = "pan.alexander.tordnscrypt/app_data/dnscrypt-proxy/public-resolvers.md";
    public static final String rules_tag = "pan.alexander.tordnscrypt/app_data/abstract_rules";

    public DialogFragment dialogFragment;
    public PreferencesTorFragment preferencesTorFragment;
    private SettingsParser settingsParser;
    private PreferencesDNSFragment preferencesDNSFragment;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        PathVars pathVars = PathVars.getInstance(this);
        String appDataDir = pathVars.getAppDataDir();

        if (savedInstanceState != null) return;

        settingsParser = new SettingsParser(this);
        settingsParser.activateSettingsParser();

        FragmentTransaction fSupportTrans = getSupportFragmentManager().beginTransaction();
        Intent intent = getIntent();
        Log.d(LOG_TAG, "SettingsActivity getAction " + intent.getAction());

        if (Objects.equals(intent.getAction(), "DNS_Pref")) {
            dialogFragment = PleaseWaitProgressDialog.getInstance();
            dialogFragment.show(getSupportFragmentManager(), "PleaseWaitProgressDialog");
            FileOperations.readTextFile(this, appDataDir + "/app_data/dnscrypt-proxy/dnscrypt-proxy.toml", dnscrypt_proxy_toml_tag);
        } else if (Objects.equals(intent.getAction(), "Tor_Pref")) {
            dialogFragment = PleaseWaitProgressDialog.getInstance();
            dialogFragment.show(getSupportFragmentManager(), "PleaseWaitProgressDialog");
            FileOperations.readTextFile(this, appDataDir + "/app_data/tor/tor.conf", tor_conf_tag);
        } else if (Objects.equals(intent.getAction(), "I2PD_Pref")) {
            dialogFragment = PleaseWaitProgressDialog.getInstance();
            dialogFragment.show(getSupportFragmentManager(), "PleaseWaitProgressDialog");
            FileOperations.readTextFile(this, appDataDir + "/app_data/i2pd/i2pd.conf", itpd_conf_tag);
        } else if (Objects.equals(intent.getAction(), "fast_Pref")) {
            PreferencesFastFragment preferencesFastFragment = new PreferencesFastFragment();
            fSupportTrans.replace(android.R.id.content, preferencesFastFragment, "fastSettingsFragment");
            fSupportTrans.commit();
        } else if (Objects.equals(intent.getAction(), "common_Pref")) {
            fSupportTrans.replace(android.R.id.content, new PreferencesCommonFragment());
            fSupportTrans.commit();
        } else if (Objects.equals(intent.getAction(), "DNS_servers_Pref")) {
            dialogFragment = PleaseWaitProgressDialog.getInstance();
            dialogFragment.show(getSupportFragmentManager(), "PleaseWaitProgressDialog");
            FileOperations.readTextFile(this, appDataDir + "/app_data/dnscrypt-proxy/dnscrypt-proxy.toml", public_resolvers_md_tag);
            FileOperations.readTextFile(this, appDataDir + "/app_data/dnscrypt-proxy/public-resolvers.md", public_resolvers_md_tag);
        } else if (Objects.equals(intent.getAction(), "open_qery_log")) {
            Bundle bundle = new Bundle();
            String path = appDataDir + "/cache/query.log";
            bundle.putString("path", path);
            ShowLogFragment frag = new ShowLogFragment();
            frag.setArguments(bundle);
            fSupportTrans.replace(android.R.id.content, frag);
            fSupportTrans.commit();
        } else if (Objects.equals(intent.getAction(), "open_nx_log")) {
            Bundle bundle = new Bundle();
            String path = appDataDir + "/cache/nx.log";
            bundle.putString("path", path);
            ShowLogFragment frag = new ShowLogFragment();
            frag.setArguments(bundle);
            fSupportTrans.replace(android.R.id.content, frag);
            fSupportTrans.commit();
        } else if (Objects.equals(intent.getAction(), "forwarding_rules_Pref")) {
            dialogFragment = PleaseWaitProgressDialog.getInstance();
            dialogFragment.show(getSupportFragmentManager(), "PleaseWaitProgressDialog");
            FileOperations.readTextFile(this, appDataDir + "/app_data/dnscrypt-proxy/forwarding-rules.txt", rules_tag);
        } else if (Objects.equals(intent.getAction(), "cloaking_rules_Pref")) {
            dialogFragment = PleaseWaitProgressDialog.getInstance();
            dialogFragment.show(getSupportFragmentManager(), "PleaseWaitProgressDialog");
            FileOperations.readTextFile(this, appDataDir + "/app_data/dnscrypt-proxy/cloaking-rules.txt", rules_tag);
        } else if (Objects.equals(intent.getAction(), "blacklist_Pref")) {
            dialogFragment = PleaseWaitProgressDialog.getInstance();
            dialogFragment.show(getSupportFragmentManager(), "PleaseWaitProgressDialog");
            FileOperations.readTextFile(this, appDataDir + "/app_data/dnscrypt-proxy/blacklist.txt", rules_tag);
        } else if (Objects.equals(intent.getAction(), "ipblacklist_Pref")) {
            dialogFragment = PleaseWaitProgressDialog.getInstance();
            dialogFragment.show(getSupportFragmentManager(), "PleaseWaitProgressDialog");
            FileOperations.readTextFile(this, appDataDir + "/app_data/dnscrypt-proxy/ip-blacklist.txt", rules_tag);
        } else if (Objects.equals(intent.getAction(), "whitelist_Pref")) {
            dialogFragment = PleaseWaitProgressDialog.getInstance();
            dialogFragment.show(getSupportFragmentManager(), "PleaseWaitProgressDialog");
            FileOperations.readTextFile(this, appDataDir + "/app_data/dnscrypt-proxy/whitelist.txt", rules_tag);
        } else if (Objects.equals(intent.getAction(), "pref_itpd_addressbook_subscriptions")) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            ArrayList<String> rules_file = new ArrayList<>();

            String subscriptionsSaved = sp.getString("subscriptions", "");

            String[] arr = {""};
            if (subscriptionsSaved != null && subscriptionsSaved.contains(",")) {
                arr = subscriptionsSaved.split(",");
            }

            String subscriptions = "subscriptions";
            for (String str : arr) {
                rules_file.add(str.trim());
            }
            fSupportTrans = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("rules_file", rules_file);
            bundle.putString("path", subscriptions);
            ShowRulesRecycleFrag frag = new ShowRulesRecycleFrag();
            frag.setArguments(bundle);
            fSupportTrans.replace(android.R.id.content, frag);
            fSupportTrans.commit();
        } else if (Objects.equals(intent.getAction(), "tor_sites_unlock")) {
            Bundle bundle = new Bundle();
            bundle.putString("deviceOrTether", "device");
            UnlockTorIpsFrag unlockTorIpsFrag = new UnlockTorIpsFrag();
            unlockTorIpsFrag.setArguments(bundle);
            fSupportTrans.replace(android.R.id.content, unlockTorIpsFrag);
            fSupportTrans.commit();
        } else if (Objects.equals(intent.getAction(), "tor_sites_unlock_tether")) {
            Bundle bundle = new Bundle();
            bundle.putString("deviceOrTether", "tether");
            UnlockTorIpsFrag unlockTorIpsFrag = new UnlockTorIpsFrag();
            unlockTorIpsFrag.setArguments(bundle);
            fSupportTrans.replace(android.R.id.content, unlockTorIpsFrag);
            fSupportTrans.commit();
        } else if (Objects.equals(intent.getAction(), "tor_apps_unlock")) {
            fSupportTrans.replace(android.R.id.content, new UnlockTorAppsFragment());
            fSupportTrans.commit();
        } else if (Objects.equals(intent.getAction(), "tor_bridges")) {
            fSupportTrans.replace(android.R.id.content, new PreferencesTorBridges(), "PreferencesTorBridges");
            fSupportTrans.commit();
        } else if (Objects.equals(intent.getAction(), "use_proxy")) {
            fSupportTrans.replace(android.R.id.content, new ProxyFragment(), "ProxyFragment");
            fSupportTrans.commit();
        } else if (Objects.equals(intent.getAction(), "proxy_apps_exclude")) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("proxy", true);
            UnlockTorAppsFragment unlockTorAppsFragment = new UnlockTorAppsFragment();
            unlockTorAppsFragment.setArguments(bundle);
            fSupportTrans.replace(android.R.id.content, unlockTorAppsFragment);
            fSupportTrans.commit();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof PreferencesDNSFragment) {
            preferencesDNSFragment = (PreferencesDNSFragment) fragment;
        } else if (fragment instanceof PreferencesTorFragment) {
            preferencesTorFragment = (PreferencesTorFragment) fragment;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (preferencesDNSFragment != null && data != null) {
            Uri[] filesUri = getFilesUri(data);

            if (filesUri.length > 0) {

                switch (requestCode) {
                    case PreferencesDNSFragment.PICK_BLACKLIST_HOSTS:
                        preferencesDNSFragment.importRules(DNSCryptRulesVariant.BLACKLIST_HOSTS, filesUri);
                        break;
                    case PreferencesDNSFragment.PICK_WHITELIST_HOSTS:
                        preferencesDNSFragment.importRules(DNSCryptRulesVariant.WHITELIST_HOSTS, filesUri);
                        break;
                    case PreferencesDNSFragment.PICK_BLACKLIST_IPS:
                        preferencesDNSFragment.importRules(DNSCryptRulesVariant.BLACKLIST_IPS, filesUri);
                        break;
                    case PreferencesDNSFragment.PICK_FORWARDING:
                        preferencesDNSFragment.importRules(DNSCryptRulesVariant.FORWARDING, filesUri);
                        break;
                    case PreferencesDNSFragment.PICK_CLOAKING:
                        preferencesDNSFragment.importRules(DNSCryptRulesVariant.CLOAKING, filesUri);
                        break;
                    default:
                        Log.e(LOG_TAG, "SettingsActivity wrong onActivityRequestCode " + requestCode);
                }

            }
        }
    }

    private Uri[] getFilesUri(Intent data) {
        Uri[] uris = new Uri[0];
        ClipData clipData = data.getClipData();

        if (clipData == null && data.getData() != null) {
            uris = new Uri[]{data.getData()};
        } else if (clipData != null) {
            uris = new Uri[clipData.getItemCount()];
            for (int i = 0; i < clipData.getItemCount(); i++) {
                Uri uri = clipData.getItemAt(i).getUri();
                uris[i] = uri;
            }
        }

        return uris;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (settingsParser != null)
            settingsParser.deactivateSettingsParser();

        dialogFragment = null;
        preferencesTorFragment = null;
        settingsParser = null;
        preferencesDNSFragment = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// API 5+ solution
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
