package com.example.fileencryptor.core.common.ui.model

import com.example.fileencryptor.core.domain.EncryptionAlgorithm


class AlgorithmDropDownModel(
    val items: List<AlgorithmDropDownItem>
) {
    inner class AlgorithmDropDownItem(
        val title: String,
        val algorithm: EncryptionAlgorithm
    )
}