/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010-2012, openHAB.org <admin@openhab.org>
 * <p>
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 * <p>
 * Additional permission under GNU GPL version 3 section 7
 * <p>
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */

package org.openhab.habdroid.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.openhab.habclient.HABApplication;
import org.openhab.habclient.rest.OpenHabService;
import org.openhab.habdroid.R;
import org.openhab.habdroid.util.Util;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by belovictor on 8/20/13.
 */

public class OpenHABInfoActivity extends Activity {
    private static final String TAG = "OpenHABInfoActivity";
    private TextView mOpenHABVersionText;
    private TextView mOpenHABUUIDText;
    private TextView mOpenHABSecretText;
    private TextView mOpenHABSecretLabel;
    private String mOpenHABBaseUrl;
    private String mUsername;
    private String mPassword;
    private CompositeDisposable disposables;

    @Inject
    OpenHabService openHabService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        ((HABApplication) getApplication()).appComponent().infoActivity().inject(this);

        Util.setActivityTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.openhabinfo);

        mOpenHABVersionText = (TextView) findViewById(R.id.openhab_version);
        mOpenHABUUIDText = (TextView) findViewById(R.id.openhab_uuid);
        mOpenHABSecretText = (TextView) findViewById(R.id.openhab_secret);
        mOpenHABSecretLabel = (TextView) findViewById(R.id.openhab_secret_label);
        if (getIntent().hasExtra("openHABBaseUrl")) {
            mOpenHABBaseUrl = getIntent().getStringExtra("openHABBaseUrl");
            mUsername = getIntent().getStringExtra("username");
            mPassword = getIntent().getStringExtra("password");
        } else {
            Log.e(HABApplication.getLogTag(), "No openHABBaseURl parameter passed, can't fetch openHAB info from nowhere");
            finish();
        }
    }

    @Override
    public void onResume() {
        Log.d(HABApplication.getLogTag(), "onResume()");
        super.onResume();
        Log.d(HABApplication.getLogTag(), "[AsyncHttpClient] url = " + mOpenHABBaseUrl + "static/version");
        disposables = new CompositeDisposable(
                openHabService.getVersion()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String content) throws Exception {
                                Log.d(HABApplication.getLogTag(), "Got version = " + content);
                                mOpenHABVersionText.setText(content);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable error) throws Exception {
                                mOpenHABVersionText.setText("Unknown");
                                Log.e(HABApplication.getLogTag(), "Could not get version", error);
                            }
                        }),
                openHabService.getUUID()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String content) throws Exception {
                                Log.d(HABApplication.getLogTag(), "Got uuid = " + content);
                                mOpenHABUUIDText.setText(content);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable error) throws Exception {
                                mOpenHABUUIDText.setText("Unknown");
                                Log.e(HABApplication.getLogTag(), "Could not get uuid", error);
                            }
                        }),
                openHabService.getSecret()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String content) throws Exception {
                        Log.d(HABApplication.getLogTag(), "Got secret = " + content);
                        mOpenHABSecretText.setVisibility(View.VISIBLE);
                        mOpenHABSecretLabel.setVisibility(View.VISIBLE);
                        mOpenHABSecretText.setText(content);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable error) throws Exception {
                        mOpenHABSecretText.setVisibility(View.GONE);
                        mOpenHABSecretLabel.setVisibility(View.GONE);
                        Log.e(HABApplication.getLogTag(), "Could not get secret", error);
                    }
                })
        );
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(disposables != null)
            disposables.dispose();
    }

    @Override
    public void finish() {
        super.finish();
        Util.overridePendingTransition(this, true);
    }
}
