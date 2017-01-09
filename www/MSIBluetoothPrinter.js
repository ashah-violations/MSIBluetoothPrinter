var exec = require('cordova/exec');

exports.print = function(str, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'MSIBluetoothPrinter', 'sendData', [str]);
};

exports.findPrinters = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'MSIBluetoothPrinter', 'findPrinter', []);
};
