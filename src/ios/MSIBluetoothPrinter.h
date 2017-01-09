
#import <Cordova/CDVPlugin.h>

@interface MSIBluetoothPrinter : CDVPlugin
- (void) sendData :(CDVInvokedUrlCommand *)command;
- (void) findPrinter :(CDVInvokedUrlCommand *)command;
@end
