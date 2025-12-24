package com.binarybhaskar.branchit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform