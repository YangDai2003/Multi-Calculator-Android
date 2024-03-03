## Privacy Policy of Multi-Calculator

This is an open source Android app developed by Yang Dai. The source code is available on GitHub under the Apache License (2.0 or later).

### Data collected by apps and third-party SDKs

Multi-Calculator itself does not collect user confidential data and personal information such as address, name and E-Mail.

Third-party SDKs will be explained in the following table:


| SDK name | Purpose of using SDK                           | Data collected by SDK                                           |
|:--------:|------------------------------------------------|-----------------------------------------------------------------|
| Firebase | Analyze application crashes and ANR situations | Device model, OS version, code crash location and error message |

[Firebase Terms of Service](https://firebase.google.com/terms)

### Permissions requested in the app

The list of permissions required by the app can be found in the `AndroidManifest.xml` file:

https://github.com/YangDai2003/Multi-Calculator-Android/blob/1796346f5eeafca8eeeb2ec2af245eff02b504b2/app/src/main/AndroidManifest.xml#L6-L11
<br/>


|                                       Permission                                        | Purpose                                                   |
|:---------------------------------------------------------------------------------------:|-----------------------------------------------------------|
|                              `android.permission.VIBRATE`                               | Used to provide vibration feedback when operating buttons |
| `android.permission.ACCESS_COARSE_LOCATION` , `android.permission.ACCESS_FINE_LOCATION` | Used to provide compass functionality                     |
|                              `android.permission.INTERNET`                              | Used to obtain real-time exchange rate data               |
|                        `android.permission.SYSTEM_ALERT_WINDOW`                         | Used to evoke third-party application windows             |
|                       `android.permission.SYSTEM_OVERLAY_WINDOW`                        | Used to provide floating window function                  |
|                        `com.google.android.gms.permission.AD_ID`                        | Used to declare firebase                                  |

 <hr style="border:1px solid gray">

If you have any questions about this Policy or personal information protection, you can send your written questions, opinions, or suggestions to the following E-Mail address: dy15800837435@gmail.com
