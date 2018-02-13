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

(function (document, $) {
    'use strict';

    // Form elements for which we don't want to capture the keystrokes so as not
    // to brake the customer experience.
    var FORM_ELEMENTS = ['BUTTON', 'DATALIST', 'INPUT', 'OPTION', 'SELECT', 'TEXTAREA'];

    // Update the ascii store with each key press
    $(document).on('keypress', function(ev) {
        if (window.ContextHub
                // Do not catch key press on form elements to not break user experience
                && FORM_ELEMENTS.indexOf(ev.target.nodeName) === -1
                // ignore non-ascii characters
                && ev.key.match(/[\x00-\x7F]/)) {
            ContextHub.setItem('asciicodes/code', ev.key);
        }
    });

}(document, window.jQuery));
