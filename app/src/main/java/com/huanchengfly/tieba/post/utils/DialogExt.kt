package com.huanchengfly.tieba.post.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun Context.showDialog(block: AlertDialog.Builder.() -> Unit = {}): AlertDialog =
    AlertDialog.Builder(this).apply(block).show()

fun Context.buildDialog(block: AlertDialog.Builder.() -> Unit = {}): AlertDialog =
    AlertDialog.Builder(this).apply(block).create()
