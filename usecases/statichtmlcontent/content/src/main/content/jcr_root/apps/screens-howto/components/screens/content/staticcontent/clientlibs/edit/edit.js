/*
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2016 Adobe Systems Incorporated
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

(function(document, $, i18n) {
    'use strict';

    var FILE_UPLOAD_ID_SELECTOR = '#screens-fileupload';

    var ui = $(window).adaptTo('foundation-ui');

    var onFileLoaded = function(event) {
        triggerUnarchiveUploadedFile(event);
    };
    $(document).on('coral-fileupload:load', FILE_UPLOAD_ID_SELECTOR, onFileLoaded);

    function triggerUnarchiveUploadedFile(event) {
        var staticContentPath = Granite.HTTP.externalize(event.detail.action + '.process.json');
        var fileRelPath = Granite.HTTP.externalize('file.sftmp');

        ui.wait();

        Granite.$.ajax({
            type: 'POST',
            async: true,
            dataType: 'json',
            url: staticContentPath,
            data: {
                'file_path': fileRelPath
            },
            success: function() {
                ui.notify(null, i18n.get('Zip uploaded.'), 'success');
                ui.clearWait();
            },
            error: function(error) {
                ui.notify(null, i18n.get('Zip could not be uploaded. ') + error.responseJSON.status, 'error');
                ui.clearWait();
            }
        });

    }

}(document, Granite.$, Granite.I18n));
