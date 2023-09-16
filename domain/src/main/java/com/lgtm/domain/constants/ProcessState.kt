package com.lgtm.domain.constants

enum class ProcessState(val order: Int) {
    WAITING_FOR_PAYMENT(0),
    PAYMENT_CONFIRMATION(1),
    MISSION_PROCEEDING(2),
    CODE_REVIEW(3),
    MISSION_FINISHED(4),
    FEEDBACK_REVIEWED(5);

    companion object {
        fun getProcessState(processState: String?): ProcessState {
            requireNotNull(processState)
            values().forEach {
                if (it.name == processState) return it
            }
            throw IllegalStateException("ProcessState not found")
        }

        fun isMissionSubmitted(processState: String?): Boolean {
            requireNotNull(processState)
            values().forEach {
                if (it.name == processState) return it.order >= CODE_REVIEW.order
            }
            throw IllegalStateException("ProcessState not found")
        }
    }
}