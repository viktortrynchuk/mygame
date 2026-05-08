package com.example.mygame.database.map

enum class FlagKind{
    NORMAL,           // garrison/parked or merged allied stacks
    SIEGE_ATTACKER,   // attacker, ringed near city
    MOVING,            // on edge between lands
    PLAYER
}