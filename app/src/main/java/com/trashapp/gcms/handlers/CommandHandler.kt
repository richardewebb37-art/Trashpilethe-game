package com.trashapp.gcms.handlers

import com.trashapp.gcms.commands.GCMSCommand
import com.trashapp.gcms.events.GCMSEvent
import com.trashapp.gcms.models.GCMSState

interface CommandHandler {
    fun canHandle(command: GCMSCommand): Boolean
    suspend fun handle(command: GCMSCommand, state: GCMSState): Result<List<GCMSEvent>>
}