package com.proscan.result_presentation

import com.proscan.result_domain.model.ScanAction

sealed class ResultEvent {
    data class ExecuteAction(val action: ScanAction) : ResultEvent()
    object ConfirmPendingAction : ResultEvent()
    object DismissWarning : ResultEvent()
    object CopyToClipboard : ResultEvent()
    object Share : ResultEvent()
    object NavigateBack : ResultEvent()
}
