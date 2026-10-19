package com.example.pipeline

import com.example.repositories.Repositories

class Pipeline {
    private val steps = mutableListOf<Step>()

    fun step(step: Step): Pipeline {
        steps.add(step)
        return this
    }

    fun run(): Repositories {
        val repositories = Repositories()
        for (step in steps) {
            step.run(repositories)
        }
        return repositories
    }
}
