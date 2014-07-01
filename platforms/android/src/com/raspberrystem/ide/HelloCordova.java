/*
	   Licensed to the Apache Software Foundation (ASF) under one
	   or more contributor license agreements.  See the NOTICE file
	   distributed with this work for additional information
	   regarding copyright ownership.  The ASF licenses this file
	   to you under the Apache License, Version 2.0 (the
	   "License"); you may not use this file except in compliance
	   with the License.  You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

	   Unless required by applicable law or agreed to in writing,
	   software distributed under the License is distributed on an
	   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
	   KIND, either express or implied.  See the License for the
	   specific language governing permissions and limitations
	   under the License.
 */

package com.raspberrystem.ide;

import android.os.Bundle;
import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.View;
import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.BaseInputConnection;
import org.apache.cordova.*;

/*
public class HelloCordova extends CordovaActivity 
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		super.init();
		super.loadUrl(Config.getStartUrl());
	}
}*/

//This is a bit of a hack to get backspace to work properly in the code editor

public class HelloCordova extends CordovaActivity {

	private class HackedWebView extends CordovaWebView{

		public HackedWebView(Context context) {
			super(context);
		}

		@Override
		public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
			HackedInputConnection connection = new HackedInputConnection(this, false);

			return connection;
		}

	}

	private class HackedInputConnection extends BaseInputConnection{

		public HackedInputConnection(View targetView, boolean fullEditor) {
			super(targetView, fullEditor);
		}

		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {	   
			// magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
			if (beforeLength == 1 && afterLength == 0) {
				// backspace
				return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
					&& super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
			}

			return super.deleteSurroundingText(beforeLength, afterLength);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init(); // Don't forget this, you'll get runtime error otherwise!

		// The following does the trick:
		super.appView.getSettings().setUseWideViewPort(true);
		super.appView.getSettings().setLoadWithOverviewMode(true);

		// Set by <content src="index.html" /> in config.xml
		super.loadUrl(Config.getStartUrl());
		//super.loadUrl("file:///android_asset/www/index.html")
		super.setIntegerProperty("loadUrlTimeoutValue", 10000); 
	}

	/**
	 * Create and initialize web container with default web view objects.
	 */
	@Override
	public void init() {
		CordovaWebView webView = new HackedWebView(this);
		CordovaWebViewClient webViewClient;
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			webViewClient = new CordovaWebViewClient(this, webView);
		}
		else {
			webViewClient = new IceCreamCordovaWebViewClient(this, webView);
		}
		this.init(webView, webViewClient, new CordovaChromeClient(this, webView));
	}

}