#import <Cocoa/Cocoa.h>
#import <jni.h>

/**
 * JNI implementation for Stealth Mode on macOS
 * This code runs in the same process as the JVM, allowing it to access
 * and modify the application's windows.
 */

JNIEXPORT void JNICALL 
Java_com_github_ericomonteiro_pirateparrotai_platform_WindowManager_nativeSetHideFromCapture
  (JNIEnv *env, jobject obj, jboolean hide) {
    
    // Execute on main thread (required for Cocoa UI operations)
    dispatch_async(dispatch_get_main_queue(), ^{
        @autoreleasepool {
            NSApplication *app = [NSApplication sharedApplication];
            NSArray *windows = [app windows];
            
            // NSWindowSharingType values:
            // NSWindowSharingNone = 0 (hidden from screen capture)
            // NSWindowSharingReadOnly = 1 (visible in screen capture)
            NSWindowSharingType type = hide ? NSWindowSharingNone : NSWindowSharingReadOnly;
            
            int count = 0;
            for (NSWindow *window in windows) {
                if ([window isVisible]) {
                    [window setSharingType:type];
                    count++;
                    NSLog(@"[Stealth] Updated window: %@", [window title]);
                }
            }
            
            NSLog(@"[Stealth] Mode %@ - Updated %d window(s)", 
                  hide ? @"ENABLED" : @"DISABLED", count);
        }
    });
}

/**
 * JNI_OnLoad - Called when library is loaded
 * Returns the JNI version required
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    NSLog(@"[Stealth] Native library loaded successfully");
    return JNI_VERSION_1_8;
}

/**
 * JNI_OnUnload - Called when library is unloaded
 */
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    NSLog(@"[Stealth] Native library unloaded");
}
