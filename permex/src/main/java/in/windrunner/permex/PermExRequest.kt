package `in`.windrunner.permex

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Vadim Sinitskiy @see https://github.com/dimskiy
 *
 * Permission request model.
 *
 * @param nameRequested - contains Android permission String.
 * For example: @link android.permission.ACCESS_FINE_LOCATION
 *
 * @param forceShowExplanation - allows to force showing rationale dialog for this particular
 * permissions request. The rationale dialog will be requested through
 * @link PermExExplanationDelegate#showConfirmationDialog
 */
@Parcelize
data class PermExRequest(
    val nameRequested: String,
    val forceShowExplanation: Boolean = false
): Parcelable