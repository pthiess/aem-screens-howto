/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2015 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */

require([
    'screens/player/shared/serviceadmin'
], function(ServiceAdmin) {
    'use strict';

    window.CQ = window.CQ || {};
    window.CQ.screens = window.CQ.screens || {};
    window.CQ.screens.serviceadmin = ServiceAdmin;

    // used by ScreensDisplay.js to detect the firmware iframe
    window.ScreensFirmware = true;

    /**
     * The global Firmware
     */
    ServiceAdmin.start();

    $(document).on('keypress', function(ev) {
        ContextHub.getStore('asciicodes').record(ev.key);
    });

})();
