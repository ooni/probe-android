package org.openobservatory.ooniprobe.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.AppLogger;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ProxyProtocol;
import org.openobservatory.ooniprobe.common.ProxySettings;

import java.net.URISyntaxException;
import java.util.Objects;

import javax.inject.Inject;

import ru.noties.markwon.Markwon;

/**
 * The ProxyActivity is part of the Settings. It allows users to
 * configure the proxy for speaking with OONI's backends.
 */
public class ProxyActivity extends AbstractActivity {
    // TODO(bassosimone): find way to write unit tests for this class.

    /*
     * Implementation note: we store the components of the URL into the settings
     * via the common.ProxySettings class. In turn, this class allows us to get
     * a URL to describe the proxy, which is what oonimkall wants.
     *
     * This comment is here to explain what is the current status of proxying
     * and what could be future improvements in this area.
     *
     * Current design
     *
     * As of 2021-05-15, the ooni/probe-cli's oonimkall recognizes these proxies:
     *
     * 1. an empty string means no proxy;
     *
     * 2. "psiphon://" means that we wanna use psiphon;
     *
     * 3. "socks5://1.2.3.4:5678" or "socks5://[::1]:5678" or "socks5://d.com:5678"
     * means that we wanna use the given socks5 proxy.
     *
     * Future improvements
     *
     * In the future, we would like to extend this design as follows.
     *
     * We want to combine psiphon and socks5. This means that we will tell psiphon to
     * use a possibly-password protected socks5 proxy. The URL will in this case be:
     *
     *     psiphon+socks5://user:password@1.2.3.4:5678/
     *     psiphon+socks5://user:password@[::1]:5678/
     *     psiphon+socks5://user:password@d.com:5678/
     *
     * This implies we can trivially support a vanilla socks5 proxy with username and
     * password by just replacing `psiphon+socks5` with `socks5`.
     *
     * We also want to support vanilla tor, using `tor://`.
     *
     * We also want to support vanilla tor with socks5, which is trivially doable
     * using as a scheme the `tor+socks5` scheme.
     *
     * We also want the user to be able to provide one or more bridges. The bridge
     * line is a string, so this can be done as follows:
     *
     *     tor:///?bridge=<bridge>&bridge=<bridge>
     *
     * where <bridge> could either be the base64 of a bridge line or alternatively
     * just a quoted bridge line (both solutions should work fine).
     *
     * Compatibility issues
     *
     * We are using a standard concept, the URI. There are some pitfalls in
     * constructing URIs, though. We try to overcome this pitfalls by parsing
     * the components of the URL and fixing the output where needed. The
     * most pressing concern at the moment is that we need to quote IPv6 addrs
     * using `[` and `]` when we're constructing a URL.
     *
     * Acknowledgments
     *
     * The design and implementation of this class owes to the code contributed
     * by and the suggestion from friendly anonymous users. Thank you!
     */
    @Inject
    AppLogger logger;
    // TAG is the tag used for logging.
    private final static String TAG = "ProxyActivity";

    @Inject
    PreferenceManager preferenceManager;
    // The following radio group describes the top level choice
    // in terms of proxying: no proxy, psiphon, or custom.

    // proxyRadioGroup is the top-level radio group.
    private RadioGroup proxyRadioGroup;

    // proxyNoneRB is the radio button selecting the "none" proxy.
    private RadioButton proxyNoneRB;

    // proxyPsiphonRB is the radio button selecting the "psiphon" proxy.
    private RadioButton proxyPsiphonRB;

    // proxyCustomRB is the radio button for the "custom" proxy.
    private RadioButton proxyCustomRB;

    // The following radio group allows users to choose which specific
    // custom proxy they would like to use. When writing this documentation,
    // only socks5 is available but we will add more options.

    // customProxyRadioGroup allows you to choose among the different
    // kinds of custom proxies that are available.
    private RadioGroup customProxyRadioGroup;

    // customProxySOCKS5 selects the custom SOCKS5 proxy type.
    private RadioButton customProxySOCKS5;

    // The following settings allow users to configure the custom proxy.

    // customProxyHostname is the hostname for the custom proxy.
    private TextInputLayout customProxyHostname;

    // customProxyPort is the port for the custom proxy.
    private TextInputLayout customProxyPort;

    // settings contains a representation of the proxy settings
    // loaded from the preference manager.
    private ProxySettings settings;

    /**
     * onCreate reads the current proxy configuration from the preference
     * manager and shows users the current configuration.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);

        // We draw the view and store references to objects needed
        // when configuring the initial view or modifying it.
        setContentView(R.layout.activity_proxy);
        proxyRadioGroup = findViewById(R.id.proxyRadioGroup);
        proxyNoneRB = findViewById(R.id.proxyNone);
        proxyPsiphonRB = findViewById(R.id.proxyPsiphon);
        proxyCustomRB = findViewById(R.id.proxyCustom);
        customProxyRadioGroup = findViewById(R.id.customProxyRadioGroup);
        customProxySOCKS5 = findViewById(R.id.customProxySOCKS5);
        customProxyHostname = findViewById(R.id.customProxyHostname);
        customProxyPort = findViewById(R.id.customProxyPort);

        // We fill the footer that helps users to understand this settings screen.
        TextView proxyFooter = findViewById(R.id.proxyFooter);
        Markwon.setMarkdown(proxyFooter, getString(R.string.Settings_Proxy_Footer));

        // We read settings and configure the initial view.
        loadSettingsAndConfigureInitialView();
    }

    // isNotBlank is a robust way to check whether an input field is
    // actually blank as documented at https://stackoverflow.com/a/48782673
    private static boolean isNotBlank(String str) {
        return (str != null && !"".equals(str.trim()));
    }

    // LoadSettingsAndConfigureInitialView loads the settings and and fills
    // the state of the view depending on the configured proxy. If, for
    // any reason including an upgrade, we don't recognize the originally stored
    // settings, then we behave like no proxy had been configured.
    private void loadSettingsAndConfigureInitialView() {
        try {
            settings = ProxySettings.newProxySettings(preferenceManager);
        } catch (ProxySettings.InvalidProxyURL exc) {
            Log.w(TAG, "newProxySettings failed: " + exc);
            logger.w(TAG, "newProxySettings failed: " + exc);
            settings = new ProxySettings(); // start over as documented
        }
        configureInitialViewWithSettings(settings);
    }

    // configureInitialViewWithSettings configures the view using the given settings.
    private void configureInitialViewWithSettings(ProxySettings settings) {
        // Inspect the scheme and use the scheme to choose among the
        // top-level radio buttons describing the proxy type.
        if (settings.protocol == ProxyProtocol.NONE) {
            proxyNoneRB.setChecked(true);
        } else if (settings.protocol == ProxyProtocol.PSIPHON) {
            proxyPsiphonRB.setChecked(true);
        } else if (settings.protocol == ProxyProtocol.SOCKS5) {
            proxyCustomRB.setChecked(true);
        } else {
            // TODO(bassosimone): this should also be reported as a bug.
            Log.w(TAG, "got an unhandled proxy scheme");
            logger.w(TAG, "got an unhandled proxy scheme");
            return;
        }

        // If the scheme is custom, then we need to enable the
        // part of the view related to custom proxies.
        customProxySetEnabled(isSchemeCustom(settings.protocol));
        customProxySOCKS5.setChecked(isSchemeCustom(settings.protocol));

        // Populate all the editable fields _anyway_ so the user
        // has the feeling that everything was just as before
        Log.d(TAG, "(from preferences) hostname: " + settings.hostname);
        logger.i(TAG, "(from preferences) hostname: " + settings.hostname);
        Log.d(TAG, "(from preferences) port: " + settings.port);
        logger.i(TAG, "(from preferences) port: " + settings.port);
        Objects.requireNonNull(customProxyHostname.getEditText()).setText(settings.hostname);
        Objects.requireNonNull(customProxyPort.getEditText()).setText(settings.port);

        // Now we need to make the top level proxy radio group interactive: when
        // we change what is selected, we need the view to adapt.
        proxyRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.proxyNone) {
                customProxySetEnabled(false);
            } else if (checkedId == R.id.proxyPsiphon) {
                customProxySetEnabled(false);
            } else if (checkedId == R.id.proxyCustom) {
                customProxySetEnabled(true);
                customProxyRadioGroup.clearCheck();
                customProxySOCKS5.setChecked(true);
            } else {
                // TODO(bassosimone): this should also be reported as a bug.
                Log.w(TAG, "unexpected state in setOnCheckedChangeListener");
                logger.w(TAG, "unexpected state in setOnCheckedChangeListener");
            }
        });

        // When we change the focus of text fields, clear any lingering error text.
        Objects.requireNonNull(customProxyHostname.getEditText()).setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                customProxyHostname.setError(null);
            }
        });
        Objects.requireNonNull(customProxyPort.getEditText()).setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                customProxyHostname.setError(null);
            }
        });
    }

    // isSchemeCustom tells us whether a given scheme is for a custom proxy.
    private boolean isSchemeCustom(ProxyProtocol protocol) {
        // This is where we need to extend the implementation of we add a new scheme
        // that will not be related to a custom proxy type.
        return protocol == ProxyProtocol.SOCKS5;
    }

    // customProxyTextInputSetEnabled is a helper function that changes the
    // state of a given custom-proxy related editable field.
    private void customProxyTextInputSetEnabled(@NonNull TextInputLayout input, boolean flag) {
        Objects.requireNonNull(input.getEditText()).setEnabled(flag);
        input.getEditText().setFocusable(flag);
        input.getEditText().setFocusableInTouchMode(flag);
    }

    // customProxySetEnabled reacts to the enabling or disabling of the custom
    // proxy group and changes the view accordingly to that.
    private void customProxySetEnabled(boolean flag) {
        customProxySOCKS5.setEnabled(flag);
        customProxyTextInputSetEnabled(customProxyHostname, flag);
        customProxyTextInputSetEnabled(customProxyPort, flag);
    }

    // isValidHostnameOrIP validates its input as an IP address or hostname.
    @SuppressWarnings("UnstableApiUsage")
    private boolean isValidHostnameOrIP(String hostname) {
        return isNotBlank(hostname)
                && (InetAddresses.isInetAddress(hostname) || InternetDomainName.isValid(hostname));
    }

    // isValidPort validates its input as a valid port.
    private boolean isValidPort(String port) {
        try {
            return isNotBlank(port) && Integer.parseInt(port) >= 0 &&
                    Integer.parseInt(port) <= 65535;
        } catch (NumberFormatException exc) {
            return false;
        }
    }

    // Implementation note: there are multiple ways to go back on Android. The
    // following set of public overridden methods attempt to ensure that we really
    // get the "go back" event and properly route it. Should we receive any
    // issue report regarding this screen not working properly when doing back,
    // then it seems this is where we need to intervene to improve.

    /**
     * onOptionsItemSelected overrides the handling of selecting options in
     * the menu so that we can route to onBackPressed.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected called");
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true; // we are consuming this event here
        }
        return false; // normal menu processing
    }

    /**
     * onKeyDown overrides what we do when a key is pressed and there is
     * no other element in the view handling it. In such a case, we check
     * whether it's "back" and we route the call to onBackPressed.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO(bassosimone): it is not 100% clear to me why in this
        // case we always call super.onKeyDown(). We may probably wanna
        // just return false in the case in which we've got the key
        // that we excepted. Either that, or I don't fully grasp the
        // problem. For this reason, I'm adding this comment.
        Log.d(TAG, "onKeyDown called");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * onBackPressed is called when the users wants to go to the previous
     * activity. This is the place where we construct a URL from the current
     * configuration and either emits an user visible error or stores
     * the valid URL components into the settings.
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: about to save proxy settings");
        logger.i(TAG, "onBackPressed: about to save proxy settings");

        // Get the hostname and port for the custom proxy.
        String hostname = Objects.requireNonNull(customProxyHostname.getEditText()).getText().toString();
        String port = Objects.requireNonNull(customProxyPort.getEditText()).getText().toString();
        settings.hostname = hostname;
        settings.port = port;

        // If no proxy is selected then just write an empty proxy
        // configuration into the settings and move on.
        if (proxyNoneRB.isChecked()) {
            settings.protocol = ProxyProtocol.NONE;
            saveSettings();
            super.onBackPressed();
            return;
        }

        // If the psiphon proxy is checked then write back the right
        // proxy configuration for psiphon and move on.
        if (proxyPsiphonRB.isChecked()) {
            settings.protocol = ProxyProtocol.PSIPHON;
            saveSettings();
            super.onBackPressed();
            return;
        }

        // validate the hostname for the custom proxy.
        if (!isValidHostnameOrIP(hostname)) {
            customProxyHostname.setError("not a valid hostname or IP");
            return;
        }

        // validate the port for the custom proxy.
        if (!isValidPort(port)) {
            customProxyPort.setError("not a valid network port");
            return;
        }

        // At this point we're going to assume that this is a socks5 proxy. We will
        // need to change the code in here when we add support for http proxies.
        settings.protocol = ProxyProtocol.SOCKS5;
        try {
            settings.getProxyString();
        } catch (URISyntaxException e) {
            // okay, then, notwithstanding our efforts it still seems that we
            // have not obtained a valid URL, so let's not proceed.
            customProxyHostname.setError("cannot construct a valid URL");
            customProxyPort.setError("cannot construct a valid URL");
            return;
        }

        // We're good, write back the URL and move on.
        saveSettings();
        super.onBackPressed();
    }

    // saveSettings stores a good URL back into the preference manager.
    private void saveSettings() {
        settings.saveProxy(preferenceManager);
        try {
            Log.d(TAG, "writing this proxy configuration: " + settings.getProxyString());
        } catch (URISyntaxException e) {
            // This error condition is rather impossible given the way in which the code
            // in onBackPressed behaves, so it's fine to just log this error.
            Log.w(TAG, "it seems a URL we just constructed and validated is not valid?! " + e);
        }
    }
}
