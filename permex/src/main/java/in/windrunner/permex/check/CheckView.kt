package `in`.windrunner.permex.check

import `in`.windrunner.permex.PermExRequest
import `in`.windrunner.permex.tools.BasePresenterView

internal interface CheckView : BasePresenterView {

    fun getPermissionGrantedState(permission: PermExRequest): Boolean

    fun getPermissionRationaleRequired(permission: PermExRequest): Boolean

    fun callUserConfirmation(permission: PermExRequest)

    fun callSystemApprove(permissions: Set<String>)

    fun cleanUp()
}