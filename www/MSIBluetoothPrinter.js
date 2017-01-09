var exec = require('cordova/exec');

exports.print = function(str, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'MSIBluetoothPrinter', 'print', [str]);
};

exports.find = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'MSIBluetoothPrinter', 'find', []);
};
