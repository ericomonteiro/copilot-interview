package com.github.ericomonteiro.pirateparrotai.screenshot

import com.github.ericomonteiro.pirateparrotai.ui.settings.RegionSelectorWindow

actual fun showRegionSelector(
    onRegionSelected: (CaptureRegion) -> Unit,
    onCancelled: () -> Unit
) {
    RegionSelectorWindow(
        onRegionSelected = onRegionSelected,
        onCancelled = onCancelled
    ).show()
}
