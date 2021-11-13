package `in`.windrunner.permex

import `in`.windrunner.permex.databinding.ActivityMainBinding
import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    private lateinit var bindings: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindings = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindings.root)
    }

    override fun onStart() {
        super.onStart()

        val explanationDelegate = object : PermExExplanationDelegate() {
            override fun showConfirmationDialog(permission: PermExRequest) {
                BottomSheetExplanationDialog(
                    onUserDecision = { isUserConfirmed ->
                        if (isUserConfirmed) {
                            confirmPermissionRequest(permission)
                        } else {
                            declinePermissionRequest(permission)
                        }
                    },
                    context = this@MainActivity
                ).show()
            }
        }

        val permyak = PermExManager.create(
            context = this,
            explanationDelegate = explanationDelegate
        ).setResultsListener { Log.d("TESTT", it.toString()) }

        bindings.btnPermissionLocation.setOnClickListener {
            permyak.requestPermissions(
                PermExRequest(Manifest.permission.ACCESS_FINE_LOCATION, forceShowExplanation = true)
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            bindings.btnPermissionLocationBg.setOnClickListener {
                permyak.requestPermissions(
                    PermExRequest(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                )
            }

            bindings.btnPermissionLocationAndLocationBg.setOnClickListener {
                permyak.requestPermissions(
                    PermExRequest(Manifest.permission.ACCESS_FINE_LOCATION),
                    PermExRequest(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                )
            }
        } else {
            bindings.btnPermissionLocationBg.isVisible = false
            bindings.btnPermissionLocationAndLocationBg.isVisible = false
        }
    }
}