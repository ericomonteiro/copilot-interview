package com.github.ericomonteiro.pirateparrotai.screenshot

expect fun showRegionSelector(
    onRegionSelected: (CaptureRegion) -> Unit,
    onCancelled: () -> Unit
)
