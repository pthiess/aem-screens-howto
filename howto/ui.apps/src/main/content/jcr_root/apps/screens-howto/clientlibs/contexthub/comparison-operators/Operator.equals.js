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
(function(ContextHub) {
    'use strict';

    ContextHub.console.log(ContextHub.Shared.timestamp(), '[loading] contexthub.segment-engine.operators - Operator.equals.js');

    // TODO move to product code base!
    /**
     * Returns true if right equals left (case sensitive).
     *
     * @param {Object} left - left side
     * @param {Object} right - right side
     * @returns {Boolean} - true if both objects are equal
     */
    var equalsString = function(left, right) {
        return right && left && left === right;
    };

    /**
     * Returns true if right equals left (case insensitive).
     *
     * @param {Object} left - left side
     * @param {Object} right - right side
     * @returns {Boolean} - true if both objects are equal
     */
    var equalsIgnoreCaseString = function(left, right) {
        return right && left && equalsString(left.toLowerCase(), right.toLowerCase());
    };

    /* comparison operators registration */
    ContextHub.SegmentEngine.OperatorManager.register('equals.string', equalsString);
    ContextHub.SegmentEngine.OperatorManager.register('equalsIgnoreCase.string', equalsIgnoreCaseString);

}(window.ContextHub));
