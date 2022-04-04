# PermEx
**FedEx for Android permissions: just tell what you need and answer a call if needed**

Allows to offload [Android permissions](https://developer.android.com/training/permissions/requesting#handle-denial) checking & requesting logic out of your app

There only thing you still need to do is creating rationale dialog to show extra explanation to the user.

## Quick start:
1. Add to project' build dependencies:
```
allprojects {
    repositories {
        maven {
            url 'https://jitpack.io'
        }
    }
}
```
2. Add to app module' build.gradle:
```
dependencies {
    implementation 'com.github.dimskiy:Permex:permex:VERSION'
    implementation 'com.github.dimskiy:Permex:permex-rx:VERSION' //RxJava support
}
```
[![](https://jitpack.io/v/dimskiy/Permex.svg)](https://jitpack.io/#dimskiy/Permex)

3. Implement *PermExExplanationDelegate* allowing your app to receive rationale dialog requests and report user's decision
4. Create *PermExManager* instance
   (Classic way) using *PermExManager.create()* and pass *PermExExplanationDelegate* implementation created before
   (RxJava way) using *PermExManagerRx.create()* and pass *PermExExplanationDelegate* implementation created before
5. Get the results
   * (Classic way) using *(PermExManager).setResultsListener()*
   * (RxJava way) using *(PermExManagerRx).observeResults()*
6. Request permission using *PermExManager.requestPermissions()*
