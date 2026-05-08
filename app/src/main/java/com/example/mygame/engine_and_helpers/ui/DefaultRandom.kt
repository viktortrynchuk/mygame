package com.example.mygame.engine_and_helpers.ui

interface RandomProvider { fun nextInt(bound: Int): Int }
class DefaultRandom : RandomProvider { 
    override fun nextInt(bound: Int) = (0 until bound).random() 
}
