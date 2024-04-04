package com.huanchengfly.tieba.post.models

import com.huanchengfly.tieba.post.ext.toJson

open class BaseBean {
    override fun toString(): String {
        return toJson()
    }
}