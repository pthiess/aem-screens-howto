/*
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

(function ($) {
    'use strict';

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
    }

    // Extend the defaut ContextHub.Store.PersistedStore
    ContextHub.Utils.inheritance.inherit(AsciiCodeStore, ContextHub.Store.PersistedStore);

    // Register the store
    ContextHub.Utils.storeCandidates.registerStoreCandidate(AsciiCodeStore, 'screens.asciicodes', 0);

    // Update the ascii store with each key press
    $(document).on('keypress', function(ev) {
        // Do not catch key press on form elements to not break user experience, and ignore non-ascii characters
        if (['BUTTON', 'DATALIST', 'INPUT', 'OPTION', 'SELECT', 'TEXTAREA'].indexOf(ev.target.nodeName) === -1
                && ev.key.match(/[\x00-\x7F]/)) {
            ContextHub.setItem('asciicodes/code', ev.key);
        }
    });

}(ContextHubJQ));
