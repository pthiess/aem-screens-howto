/**
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2018 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */

(function (document, ContextHub) {
    'use strict';

    // Form elements for which we don't want to capture the keystrokes so as not
    // to brake the customer experience.
    var FORM_ELEMENTS = ['BUTTON', 'DATALIST', 'INPUT', 'OPTION', 'SELECT', 'TEXTAREA'];

    /**
     * The AsciiCode store is a PersistedStore holding the ASCII character of the last keystroke.
     *
     * @param       {String} name   The name of the store.
     * @param       {Object} config An object that contains configuration properties for the store
     * @constructor
     */
    function AsciiCodeStore(name, config) {
        this.config = Object.assign({}, config);
        this.init(name, this.config);

        // Update the ascii store with each key press
        document.addEventListener('keypress', function(ev) {
            if (FORM_ELEMENTS.indexOf(ev.target.nodeName) === -1 // Do not catch key press on form elements to not break user experience
                    && ev.key.match(/[\x00-\x7F]/)) { // ignore non-ascii characters
                ContextHub.setItem(name + '/code', ev.key);
            }
        }, false);
    }

    // Extend the defaut ContextHub.Store.PersistedStore
    ContextHub.Utils.inheritance.inherit(AsciiCodeStore, ContextHub.Store.PersistedStore);

    // Register the store
    ContextHub.Utils.storeCandidates.registerStoreCandidate(AsciiCodeStore, 'screens.asciicodes', 0);

}(document, window.ContextHub));
