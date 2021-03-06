/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.reference.browser.browser

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import mozilla.components.browser.domains.autocomplete.DomainAutocompleteProvider
import mozilla.components.browser.toolbar.BrowserToolbar
import mozilla.components.concept.storage.HistoryStorage
import mozilla.components.feature.toolbar.ToolbarAutocompleteFeature
import mozilla.components.feature.toolbar.ToolbarFeature
import org.mozilla.reference.browser.ext.components

class ToolbarIntegration(
    context: Context,
    toolbar: BrowserToolbar,
    historyStorage: HistoryStorage,
    domainAutocompleteProvider: DomainAutocompleteProvider,
    sessionId: String? = null
) : LifecycleObserver {
    init {
        toolbar.setMenuBuilder(context.components.toolbar.menuBuilder)

        ToolbarAutocompleteFeature(toolbar).apply {
            addHistoryStorageProvider(historyStorage)
            addDomainProvider(domainAutocompleteProvider)
        }
    }

    private val toolbarFeature: ToolbarFeature = ToolbarFeature(
        toolbar,
        context.components.core.sessionManager,
        context.components.useCases.sessionUseCases.loadUrl,
        { searchTerms -> context.components.useCases.searchUseCases.defaultSearch.invoke(searchTerms) },
        sessionId
    )

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        toolbarFeature.start()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        toolbarFeature.stop()
    }
}
