package com.example.pipeline

import com.example.repositories.Repositories

fun interface Step {
    fun run(repositories: Repositories)
}
