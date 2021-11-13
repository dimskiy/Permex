#PermEx
Allows to offload [Android permissions](https://developer.android.com/training/permissions/requesting#handle-denial) checking & requesting logic out of your app

There only thing you still need to do is creating rationale dialog to show extra explanation to the user.

Quick start:
1. Implement *PermExExplanationDelegate* allowing your app to receive rationale dialog requests and report user's decision
2. Create *PermExManager* instance using *PermExManager.create()* and pass *PermExExplanationDelegate* implementation created before
3. Get the results using *PermExManager.setResultsListener()*