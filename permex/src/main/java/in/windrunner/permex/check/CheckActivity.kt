package `in`.windrunner.permex.check

import `in`.windrunner.permex.PermExRequest
import `in`.windrunner.permex.tools.ServiceLocator
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

internal class CheckActivity : AppCompatActivity(), CheckView {

    private val presenter = ServiceLocator.checkPresenter
    private val explanationDelegate = ServiceLocator.explanationDelegate

    private val permissionsPendingRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        presenter::onPermissionsRequestingResults
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter.onAttach(this)
    }

    override fun getPermissionGrantedState(permission: PermExRequest): Boolean =
        with(ContextCompat.checkSelfPermission(this, permission.nameRequested)) {
            this == PERMISSION_GRANTED
        }

    override fun getPermissionRationaleRequired(permission: PermExRequest): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            shouldShowRequestPermissionRationale(permission.nameRequested)
        } else false

    override fun callUserConfirmation(permission: PermExRequest) {
        explanationDelegate.showConfirmationDialog(permission)
        finish()
    }

    override fun callSystemApprove(permissions: Set<String>) {
        permissionsPendingRequest.launch(permissions.toTypedArray())
    }

    override fun cleanUp() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDetach()
    }

    companion object {
        fun checkAndRequestPermissions(context: Context) {
            with(Intent(context, CheckActivity::class.java)) {
                flags = FLAG_ACTIVITY_NEW_TASK
                context.startActivity(this)
            }
        }
    }
}