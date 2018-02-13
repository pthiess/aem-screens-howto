/*
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2017 Adobe Systems Incorporated
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

    var ASCII_PROP = 'key';

    var defaultConfig = {};

    /**
     * The AsciiCode store is a PersistedStore holding product which have been recently viewed.
     */
    function AsciiCodeStore(name, config) {
        this.config = Object.assign({}, defaultConfig, config);
        this.init(name, this.config);
    }

    ContextHub.Utils.inheritance.inherit(AsciiCodeStore, ContextHub.Store.PersistedStore);

    AsciiCodeStore.prototype.record = function(ascii) {
        this.setItem(ASCII_PROP, ascii);
    };

    AsciiCodeStore.prototype.reset = function() {
        this.setItem(ASCII_PROP, null);
    };

    Object.defineProperty(AsciiCodeStore.prototype, "ascii", {
        get: function ascii() {
            return this.getItem(ASCII_PROP);
        }
    });

    ContextHub.Utils.storeCandidates.registerStoreCandidate(AsciiCodeStore, 'screens.asciicodes', 0);

    // Update the ascii store with each key press
    $(document).on('keypress', function(ev) {
        // Do not catch key press on form elements to not break user experience
        if (['BUTTON', 'DATALIST', 'INPUT', 'OPTION', 'SELECT', 'TEXTAREA'].indexOf(ev.target.nodeName) === -1) {
            ContextHub.setItem('asciicodes/key', ev.key);
        }
    });

}(ContextHubJQ));
