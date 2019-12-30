package com.project.app.Interfaces

import com.project.app.Objects.QuestionSnapshot

interface SnapshotHelper {
    fun onSnapshotsReady(result: List<QuestionSnapshot>)
}