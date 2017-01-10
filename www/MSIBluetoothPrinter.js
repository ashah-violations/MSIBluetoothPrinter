var exec = require('cordova/exec');

exports.print = function(str,mac, successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'MSIBluetoothPrinter', 'sendData', [str,mac]);
};

exports.findPrinters = function(successCallback, errorCallback) {
    cordova.exec(successCallback, errorCallback, 'MSIBluetoothPrinter', 'findPrinter', []);
};
